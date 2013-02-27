(ns kvergjelme-client.audio
  (:import [com.xuggle.xuggler
            IStreamCoder IAudioResampler IRational
            IStreamCoder$Direction ICodec$ID]))

(set! *warn-on-reflection* true)

(defn make-decoder
  []
  (doto (IStreamCoder/make IStreamCoder$Direction/DECODING)
    (.setCodec ICodec$ID/CODEC_ID_AMR_NB)
    (.setSampleRate 8000)
    (.setChannels 1)
    (.setTimeBase (IRational/make 1 1000))))

(defn make-resampler
  []
  ; signature - out channels, in channels, out sample rate, in sample rate
  (IAudioResampler/make 1 1 11025 8000))