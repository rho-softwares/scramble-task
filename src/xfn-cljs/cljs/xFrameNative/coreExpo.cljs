(ns xFrameNative.coreExpo
  (:require [reagent.core      :as r :refer [as-element]]
;            ["react-native-unimodules"   :as ec     :refer [Constants]]
            [xFrameNative.core :as xc :refer [commonInit]]
            ["expo"            :as e :refer [registerRootComponent]]
            [shadow.expo       :as expo]
            [main.app          :as ma  :refer [app-root]]
            ))

;(defn startExpoUI {:dev/after-load true} [app-root]
;  ;(defn ^:dev/after-load start []
;    (expo/render-root (r/as-element [app-root])))
;(defn startExpoUI
;  {:dev/after-load true}
;  [app-root]
;  (expo/render-root (as-element [app-root])))

(defn registerExpoApp [app-root]
  ;(startExpoUI app-root)
  (registerRootComponent #(as-element [app-root])))

(defn init_expo []
  (commonInit)
  (registerExpoApp app-root))