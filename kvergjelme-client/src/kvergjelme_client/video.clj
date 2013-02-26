(ns kvergjelme-client.video
  (:import [com.xuggle.xuggler
            IStreamCoder IRational IVideoResampler
            ICodec$ID IStreamCoder$Direction IStreamCoder$Flags IPixelFormat$Type]))

(defn make-decoder
  [fps width height]
  (doto (IStreamCoder/make IStreamCoder$Direction/DECODING)
    (.setCodec ICodec$ID/CODEC_ID_MPEG4)
    (.setPixelType IPixelFormat$Type/YUV420P)
    (.setWidth width)
    (.setHeight height)
    (.setTimeBase (IRational/make 1 fps))
    (.setFrameRate (IRational/make fps 1))))

(defn make-encoder
  [container fps width height]
  (let [stream (.addNewStream container 0)
        codec  (.getOutputDefaultVideoCodec (.getContainerFormat container))]
    (doto (.getStreamCoder stream)
      (.setCodec codec)
      (.setWidth width)
      (.setHeight height)
      (.setPixelType IPixelFormat$Type/YUV420P)
      (.setNumPicturesInGroupOfPictures 12)
      (.setProperty "nr" 0)
      (.setProperty "mbd" 0)
      (.setTimeBase (IRational/make 1 fps))
      (.setFrameRate (IRational/make fps 1))
      (.setFlag IStreamCoder$Flags/FLAG_QSCALE true))))

(defn make-resampler
  [out-width out-height in-width in-height]
  (IVideoResampler/make out-width out-height IPixelFormat$Type/YUV420P 
                        in-width in-height IPixelFormat$Type/YUV420P))