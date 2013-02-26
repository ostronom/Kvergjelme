(ns kvergjelme-client.core
  (:require [kvergjelme-client.video :as video]
            [kvergjelme-client.audio :as audio]
            [kvergjelme-client.robot :as robot])
  (:import [com.xuggle.xuggler
            IContainer IContainerFormat IPacket IVideoPicture
            IContainer$Type]))

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
  (let [first-timestamp (System/currentTimeMillis)
        resampled-picture (IVideoPicture/make
                            (.getOutputPixelFormat vresampler)
                            (.getOutputWidth vresampler)
                            (.getOutputHeight vresampler))]
    (while true
      (let [timestamp (* (- (System/currentTimeMillis) first-timestamp) 1000)
            in-picture (robot/get-screenshot-frame robot area converter timestamp)
            out-packet (IPacket/make)]
        ;(.resample vresampler resampled-picture in-picture)
        (if (>= (.encodeVideo vencoder out-packet in-picture 0) 0)
          (if (.isComplete out-packet) 
            (if (>= (.writePacket container out-packet true) 0)
              (println "Packet sent")
              (println "Packet failure")))
          (throw (RuntimeException. "Encoding video error [core/run-stream-loop]")))))))


(defn- grab-and-stream
  [fps input-width input-height output-width output-height]
  (let [vdecoder (video/make-decoder fps input-width input-height)
        adecoder (audio/make-decoder)
        vresampler (video/make-resampler input-width input-height output-width output-height)
        aresampler (audio/make-resampler)
        container (make-output-container "rtmp://localhost:1935/live" "flv")
        vencoder (video/make-encoder container fps output-width output-height)
        robot (robot/start)
        area (robot/capture-area 1280 760)
        converter (robot/create-converter area)]
    (if (>= (.open vencoder) 0)
      (do
        (.writeHeader container)
        (run-stream-loop fps container robot area converter vencoder vresampler)
        (.writeTrailer container))
      (throw (RuntimeException. "Unable to open encoder. [core/run]")))))

(defn -main
  []
  (grab-and-stream 25 1280 760 1280 760))
