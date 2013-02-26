(ns kvergjelme-client.robot
  (:import [java.awt
            Rectangle AWTException Robot]
           [java.awt.image BufferedImage]
           [com.xuggle.xuggler IVideoPicture IPixelFormat$Type]
           [com.xuggle.xuggler.video ConverterFactory IConverter]))

(defn start
  " Start desktop monitoring bot. "
  []
  (try
    (new Robot)
    (catch AWTException e
      (throw (RuntimeException.
        (str "Unable to instantiate monitoring bot" (.getMessage e) " [robot/start]"))))))

(defn capture-area
  [width height]
  (Rectangle. width height))

(defn create-converter
  [rect]
  (ConverterFactory/createConverter 
    "XUGGLER-BGR-24"
    IPixelFormat$Type/YUV420P
    (.getWidth rect)
    (.getHeight rect)))

(defn- cast-image
  [image target-type]
  (if (= (.getType image) target-type)
    image
    (let [new-image (BufferedImage. (.getWidth image) (.getHeight image) target-type)
          graphics  (.getGraphics new-image)]
      (do
        (.drawImage graphics image 0 0 nil)
        new-image))))

(defn- get-screenshot
  [robot rect]
  (cast-image (.createScreenCapture robot rect) BufferedImage/TYPE_3BYTE_BGR))

(defn get-screenshot-frame
  [robot rect converter timestamp]
  (.toPicture converter (get-screenshot robot rect) timestamp))