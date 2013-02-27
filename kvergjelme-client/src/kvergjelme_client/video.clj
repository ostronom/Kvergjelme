(ns kvergjelme-client.video
  (:import [com.xuggle.xuggler
            IStream IStreamCoder IRational IVideoResampler IContainer
            ICodec$ID IStreamCoder$Direction IStreamCoder$Flags IPixelFormat$Type]))

(set! *warn-on-reflection* true)

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
  [^IContainer container fps width height]
  (let [stream (.addNewStream container 0)
        ^ICodec$ID codec (.getOutputDefaultVideoCodec (.getContainerFormat container))]
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
  [in-width in-height out-width out-height]
  (IVideoResampler/make out-width out-height IPixelFormat$Type/YUV420P 
                        in-width in-height IPixelFormat$Type/YUV420P))