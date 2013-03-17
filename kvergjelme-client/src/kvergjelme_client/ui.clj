(ns kvergjelme-client.core
  (:require [seesaw.bind :as bind])
  (:use [seesaw core border table mig]))


(defn make-frame 
  []
  (frame
    :title "Kvergjelme-client"
    :size  [600 :by 600]
    :on-close :exit
    :menubar (menubar :items [(menu :text "View" :items [(menu-item :class :refresh)])])
    :content (border-panel
               :border 5
               :hgap 5
               :vgap 5
               :north  (make-toolbar)
               :center (make-tabs)
               :south (label :id :status :text "Ready"))))


(defn start-ui
  []
  (invoke-later 
    (-> 
      (make-frame)
      add-behaviors
      show!)))