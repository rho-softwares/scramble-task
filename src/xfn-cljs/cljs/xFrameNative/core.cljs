;(global-company-mode)
(ns xFrameNative.core
  (:require [re-frame.core               :as rf     :refer [dispatch-sync]]
            [xFrameNative.handlersFX     :as xhf]
            [xFrameNative.fsmInterpreter :as xfi]
            [xFrameNative.actions        :as xa]
            [main.actions                :as ma]
            [main.effects                :as me]))

(defn commonInit []
  (println "TODO: Uncomment commonInit for Expo and Pure-RN")
  (dispatch-sync [:initialize-db])
  (dispatch-sync [:run-first-dispatches]))