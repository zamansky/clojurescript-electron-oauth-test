(ns renderer.renderer
  (:require
   [reagent.core :as reagent :refer [atom]]
   [clojure.string :as string :refer [split-lines]]
   ))

(def electron (js/require "electron"))
(def ipcRenderer (.-ipcRenderer electron)) 

(def token (atom "unknown"))

(defn root-component []
[:div
 [:h1.font-bold.text-3xl "HELLO"]
 [:h1.font-bold.text-3xl (str "The Token is: " @token)]
 ])



(defn start! []
  (.on ipcRenderer "token" #(reset! token (js->clj %2)))
  (.send ipcRenderer "get-token" "")
  (reagent/render
   [root-component]
   (js/document.getElementById "app-container")))

(start!)

