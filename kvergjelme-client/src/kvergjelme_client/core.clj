(ns kvergjelme-client.core
  (:use [clojure.data.zip.xml :only (attr text xml->)])
  (:require [clojure.xml :as xml]
            [clojure.zip :as zip]
            [kvergjelme-client.video :as video]
            [kvergjelme-client.audio :as audio]
            [kvergjelme-client.robot :as robot])
  (:import [com.xuggle.xuggler
            IContainer IContainerFormat IPacket IVideoPicture IError
            IContainer$Type])
  (:gen-class))

(defrecord Config [url width height fps])

(defn- make-output-container
  [url format-name]
  (let [out-container (IContainer/make)
        out-format    (doto (IContainerFormat/make) (.setOutputFormat format-name url nil))]
    (try
      (if (>= (.open out-container url IContainer$Type/WRITE out-format) 0)
        out-container
        (throw (RuntimeException. "Could not open output container. [core/make-output]"))))))

(defn- run-stream-loop
  [fps container robot area converter vencoder vresampler]
  (let [first-timestamp   (System/currentTimeMillis)
        out-packet        (IPacket/make)]
    (while true
      (let [timestamp  (* (- (System/currentTimeMillis) first-timestamp) 1000)
            in-picture (robot/get-screenshot-frame robot area converter timestamp)
            out-packet (IPacket/make)
            resampled-picture (IVideoPicture/make
                                (.getOutputPixelFormat vresampler)
                                (.getOutputWidth vresampler)
                                (.getOutputHeight vresampler))]
        (.resample vresampler resampled-picture in-picture)
        (if (>= (.encodeVideo vencoder out-packet resampled-picture 0) 0)
          (do
            (.delete resampled-picture)
            (if (.isComplete out-packet)
              (let [ret-val (.writePacket container out-packet true)]
                (if (< ret-val 0)
                  (println (str "Packet failure: " (IError/make ret-val)))))))
          (throw (RuntimeException. "Encoding video error [core/run-stream-loop]")))))))


(defn- grab-and-stream
  [config]
  (let [robot         (robot/start)
        area          (robot/capture-area)
        input-width   (.width area)
        input-height  (.height area)
        output-width  (:width config)
        output-height (:height config)
        vresampler    (video/make-resampler input-width input-height output-width output-height)
        ;aresampler    (audio/make-resampler)
        container     (make-output-container (:url config) "flv")
        vencoder      (video/make-encoder container (:fps config) output-width output-height)
        converter     (robot/create-converter area)]
    (if (>= (.open vencoder) 0)
      (do
        (.writeHeader container)
        (run-stream-loop (:fps config) container robot area converter vencoder vresampler)
        (.writeTrailer container))
      (throw (RuntimeException. "Unable to open encoder. [core/run]")))))

(defn- get-config
  [filename]
  (let [zipped (zip/xml-zip (xml/parse filename))]
    (Config.
      (text (first (xml-> zipped :stream :url)))
      (Integer/parseInt (text (first (xml-> zipped :stream :width))))
      (Integer/parseInt (text (first (xml-> zipped :stream :height))))
      (Integer/parseInt (text (first (xml-> zipped :stream :fps)))))))

(defn -main
  []
  (grab-and-stream (get-config "config.xml")))
