(ns xFrameNative.actions
  (:require [re-frame.core :as rf   :refer [reg-fx reg-event-db reg-event-fx
                                            dispatch dispatch-sync]]))

(reg-event-fx
  :navigate-action
  (fn [coeffects [_ userData]]
    (let [ DataLoad   (get (get-in coeffects [:event ]) 1)
          props       (get-in DataLoad [:Props])
          navigation  (get props :navigation)
          screen      (get userData :MachineLoad)]
      {:navigate-fx [navigation screen]}
      )))

; -------------- ALERT FX -----------------
; Parece que é malandragem... pq a lambda deveria responder {:db :fx}...
;vc cortou caminho antes de precisar do reg-fx (another function = another level of indirection = another level of abstraction <3)
(reg-event-fx
  :alert-action
  (fn [coeffects [_ userData]]
    (let [  ;DataLoad (get (get-in coeffects [:event ]) 1)
            ;props (get-in DataLoad [:Props])
            msg (get userData :MachineLoad)]
      ;(println "userData: " userData)
      ;(println "msg: " msg)
      {:alert-fx msg})))

(reg-event-fx
  :print-action
  (fn [coeffects [_ userData]]
    ;(println "userData" userData)
    (let [ ;DataLoad (get (get-in coeffects [:event ]) 1)
           ;props (get-in DataLoad [:Props])
           msg (get userData :MachineLoad)]
        {:print-fx msg})))


(reg-event-fx
  :set-variable-action
  (fn [coeffects [_ userData]]
    (let [msg (get userData :MachineLoad)]
      {:set-variable-fx msg})))

(reg-event-fx
  :associn-variable-action
  (fn [coeffects [_ userData]]
    (let [msg (get userData :MachineLoad)]
      {:associn-variable-fx msg})))

(reg-event-fx
  :toggle-drawer-action
  (fn [coeffects [_ userData]]
    (let [ DataLoad   (get (get-in coeffects [:event ]) 1)
          props       (get-in DataLoad [:Props])
          navigation  (get props :navigation)
          ;screen      (get userData :MachineLoad)
          ]
      {:toggle-drawer-fx navigation})))