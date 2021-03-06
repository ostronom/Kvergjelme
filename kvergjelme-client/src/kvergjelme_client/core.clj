(ns kvergjelme-client.core
  (:use [clojure.data.zip.xml :only (attr text xml->)])
  (:require [kvergjelme-client.ui :as ui]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [kvergjelme-client.video :as video]
            [kvergjelme-client.audio :as audio]
            [kvergjelme-client.robot :as robot])
  (:import [com.xuggle.xuggler
            IContainer IContainerFormat IPacket IVideoPicture
            IError IVideoResampler IStreamCoder
            IContainer$Type]
           [java.awt Rectangle])
  (:gen-class))

(set! *warn-on-reflection* true)

(defrecord Config [url width height fps])

(defn- make-output-container
  [^String url ^String format-name]
  (let [out-container (IContainer/make)
        out-format    (doto (IContainerFormat/make) (.setOutputFormat format-name url nil))]
    (try
      (if (>= (.open out-container url IContainer$Type/WRITE out-format) 0)
        out-container
        (throw (RuntimeException. "Could not open output container. [core/make-output]"))))))

(defn- run-stream-loop
  [fps ^IContainer container robot area converter ^IStreamCoder vencoder ^IVideoResampler vresampler]
  (let [first-timestamp   (System/currentTimeMillis)
        out-packet        (IPacket/make)
        resampled-picture (IVideoPicture/make
                            (.getOutputPixelFormat vresampler)
                            (.getOutputWidth vresampler)
                            (.getOutputHeight vresampler))]
    (while true
      (let [timestamp  (* (- (System/currentTimeMillis) first-timestamp) 1000)
            in-picture (robot/get-screenshot-frame robot area converter timestamp)
            out-packet (IPacket/make)]
        (do
          (.resample vresampler resampled-picture in-picture)
          (let [enc-ret-val (.encodeVideo vencoder out-packet resampled-picture 0)]
            ;(.delete resampled-picture)
            (if (>= enc-ret-val 0)
              (if (.isComplete out-packet)
                (let [pack-ret-val (.writePacket container out-packet true)]
                  (if (< pack-ret-val 0)
                    (println (str "Packet failure: " (IError/make pack-ret-val))))))
              (throw (RuntimeException. "Encoding video error [core/run-stream-loop]")))))))))


(defn- grab-and-stream
  [^Config config]
  (let [robot         (robot/start)
        ^Rectangle area (robot/capture-area)
        input-width   (.width area)
        input-height  (.height area)
        output-width  (:width config)
        output-height (:height config)
        vresampler    (video/make-resampler input-width input-height output-width output-height)
        ;aresampler    (audio/make-resampler)
        ^IContainer container     (make-output-container (:url config) "flv")
        ^IStreamCoder vencoder      (video/make-encoder container (:fps config) output-width output-height)
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
  ;(grab-and-stream (get-config "config.xml")))
  (ui/start-ui))