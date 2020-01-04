(ns main.main
  (:require ["electron" :refer [app BrowserWindow crashReporter]]
            [cljs.core.async :refer (chan put! <! >! go go-loop timeout)]
            [cljs-http.client :as http]
            [ajax.core :refer [GET POST]]
            ))

(def main-window (atom nil))
(def auth-window (atom nil))

(def auth-url "https://github.com/login/oauth/authorize?client_id=3056f749c2e91ce4d780&scope=admin:org delete_repo")

(def id "3056f749c2e91ce4d780")
(def secret  "a94c6cac3ba9a2f13b4c112a762169bc18bfd7a1")
(def scope "repo admin:org delete_repo")

;; POST https://github.com/login/oauth/access_token
;; client_id
;; client_secret
;; code
;; Accept: application/json


(defn extract-code [url]
  (second  (re-matches #".*code=([0-9a-f]+).*" url))
  )

(defn get-token [url]
  (let  [code (extract-code url)
         url "https://github.com/login/oauth/access_token"
         ]
    (prn " code "code)
    (prn " url "url)
    (go (let [response
              (<! (http/get url {:query-params {"client_id" id "client_secret" secret "code" code }
                                 :headers {"Accept" "application/json"}
                                 }))
              ]
          ;;       (prn "HELLO")
          (prn (str (keys response)) )
          (prn  "THE RESPONSE: " (:body response))
          (.loadURL @auth-window (str "file://" js/__dirname "/public/index.html"))
          ;;(GET url {:params {:client_id id :client_secret secret :code code}
          ;;          :handler handler
          ;;          })
          ))))


(defn init-browser []
  (reset! auth-window (BrowserWindow.
                       (clj->js {:width 1200
                                 :height 1200
                                 ;;"node-integration" false
                                 ;;"web-security" false
                                 })))

  ;;(.show @auth-window)
  (.catch (.loadURL @auth-window auth-url)
          (fn [x]
            (prn (get-token (.toString  (js->clj x))))
            ;;(.log js/console  x))
            ))
  
  ;;(.webContents.on @auth-window "did-fail-load" #(.log js/console (.sender.history %)))
  (.on @auth-window "load" #(prn %1))
  (.on @auth-window "closed" #(reset! main-window nil))
  (.webContents.on @auth-window  "will-navigate" #(do
                                                    (prn  %1 %2  )
                                                    ( get-token %2))))

;; webView.getSettings().setJavaScriptEnabled(true);
;; authWindow.webContents.on('will-navigate', function (event, newUrl) {
;;   console.log(newUrl);
(defn main []
                                        ; CrashReporter can just be omitted
(.start crashReporter
(clj->js
 {:companyName "MyAwesomeCompany"
  :productName "MyAwesomeApp"
  :submitURL "https://example.com/submit-url"
  :autoSubmit false}))

(.on app "window-all-closed" #(when-not (= js/process.platform "darwin")
                                (.quit app)))
(.on app "ready" init-browser))


