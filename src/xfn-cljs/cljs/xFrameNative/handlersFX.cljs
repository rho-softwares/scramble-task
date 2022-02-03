; /// <3 <3 Localized (i.e. controlled) SIDE EFFECTS = Dirty Code = AsyncCode
;// Aqui que importamos External Code (ex NPM modules)
; //e Maybe need help of appliedscience/jsInterop
;/q.e.d. No need of UNIT TESTS because they are Pure=DeterministicFunctions (= MONADAS)
;     // THEY ALSO dont need testings because PureFunctions are so simple,
;     // they only do what is relevant to the business interests
;// MONADA = Take a copy of the world -> Return an alterated form of the world)
(ns xFrameNative.handlersFX
  (:require [re-frame.core                 :as rf   :refer [reg-fx reg-event-fx
                                                            dispatch dispatch-sync]]
            [applied-science.js-interop    :as j]
            [clojure.core.async                     :refer  [go]]
            ["axios"                       :as axios]
            ["public-ip"                    :as publicIp]
            ["validator"                    :as validator]
            ["./js_helpers/asyncAwait.js"  :as asyncAwait]))


(reg-fx
  :alert-fx
  (fn [msg] (js/alert msg)))

(reg-fx
  :print-fx
  (fn [msg] (println msg)))

(reg-fx
  :navigate-fx
  (fn [args ]
    (let [navigation  (first args)
          screen      (second args)]
      ;(.navigate navigation screen)
      (j/call-in navigation [:navigate] screen))))

(reg-fx
  :set-variable-fx
  (fn [msg]
    (let [operator (first msg)
          dataLoad (last msg)]
      (if (= operator :update-in)
          (let []
            ;(println "dataLoad:" dataLoad)
            ;(dispatch [:updateIN-DB-EvHandler [:SettingsMachine :isDarkMode? :not]])
            (dispatch [:updateIN-DB-EvHandler dataLoad]))
          (println "nao eh update-in"))
      )))

; TODO: Merge :set-variable-fx and :associn-variable-fx
(reg-fx
  :associn-variable-fx
  (fn [msg]
    ;(println "msg:" msg)
    ;[:SettingsMachine :clock newDate]
    (dispatch [:assocIN-DB-EvHandler msg])
    ))

(reg-fx
  :toggle-drawer-fx
  (fn [navigation]
    (let []
      (println "Toggling Drawer menu :)")
      (j/call-in navigation [:toggleDrawer]))))