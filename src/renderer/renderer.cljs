(ns renderer.renderer
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string :refer [split-lines]]))
(defn root-component []
  [:div
   [:h1.font-bold.text-3xl "HELLO"]
   ])

(defn start! []
  (reagent/render
    [root-component]
    (js/document.getElementById "app-container")))

(start!)

