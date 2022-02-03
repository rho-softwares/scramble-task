(ns main.actions
  (:require [re-frame.core              :as rf :refer [reg-event-fx]]
            [applied-science.js-interop :as j]))

(reg-event-fx
  :scramble-action
  (fn [coeffects [_ userData]]
    (let [;ffs     (println (str "coeffects: " coeffects))
          ScrambleVars   (get (get (get (get coeffects :db) :STATE_MACHINES)
                                         :ScrambleMachine) :LOCAL_STATE_VARS)
          string1        (get ScrambleVars :string1)
          string2        (get ScrambleVars :string2)]
      {:scramble-fx [string1 string2]})))