(ns xFrameNative.handlersDB
  (:require [re-frame.core                 :as rf   :refer [reg-sub reg-event-db
                                                            ; Obs CRIME = reg-event-fx aqui!
                                                            ; arrumar!
                                                            ;deveria ser soh reg-event-db !
                                                            reg-event-fx dispatch]]
   ;["react-native-unimodules"     :as rnu  :refer [Constants]] ; Todo Move Constatns to FX
            ;[xFrameNative.handlersFX  :as mxhf :refer [registerFirstDispatchesRun]]
            ))

(println "Entrou handerlsDB!")

(defn registerFirstDispatchesRun [args]
  (reg-event-fx
    :run-first-dispatches
    (fn [_ _]
      (let [appDB (first args)]
        ; execMachinesInitialDispatchesSync
        ; *** Create dispach-sync for each machine that has the :ON-INIT-DISPATCH-SYNC property
        (doseq [k (keys (get-in ;app-db
                              appDB
                              [:STATE_MACHINES]))]
          (if (contains? (get-in ;app-db
                              appDB
                              [:STATE_MACHINES k]) :ON-INIT-DISPATCH-SYNC)
              (let [initialList (get-in ;app-db
                                      appDB
                                      [:STATE_MACHINES k :ON-INIT-DISPATCH-SYNC])]
                (doseq [dispatchMachineAndSignal initialList]
                  (dispatch [:universalMachineEvHandler dispatchMachineAndSignal])))))))))

(defn regSubWithNamespace [args]
  (let [MachineID (first args)]
    (let [FinalNamespacedSubscriberID (keyword (name MachineID) (name :LOCAL_STATE_VARS))]
      (reg-sub FinalNamespacedSubscriberID
        (fn [db _] (get-in db [ :STATE_MACHINES MachineID :LOCAL_STATE_VARS]))))))

(defonce app-db {
  :STATE_MACHINES       { }
  :DEFAULT_SIGNAL_MSG   { :Machine-ID :emptyValue
                          :Signal-ID  :emptyValue
                          :Props     :EmptyValue}
  :COMMON_FSM_OUTPUTS   { :errors         []
                          :finalDispatchN []
                          :target-state :emptyValue}})

(defn _loadMachinesToAppDB [machinesList]
  (let [MACHINES_MAP (reduce
                      (fn [finalMap machine] (assoc finalMap (get-in machine [:MACHINE_ID])
                                                            (get-in machine [:FSM_SCHEME])))
                      {} machinesList)
       app-db-atualizado (assoc-in app-db [:STATE_MACHINES] MACHINES_MAP)]

    (reg-event-db :initialize-db (fn [_ _] app-db-atualizado))

    ; *** Register Automatic Namespaced Subscraibers
    (doseq [k (keys (get-in app-db-atualizado [:STATE_MACHINES]))] (regSubWithNamespace [k]))

    (registerFirstDispatchesRun [ app-db-atualizado ])))

; ---------- SET_NEXT_STATE Event Handlers ------------------------------------------------------
; Internal use of FSM.... final DEV should not touch this
; conclusao: always 2-arity array (:MachineID ;NEXT_STATE)
(reg-event-db
  :SetNextStateEvHandler
  (fn [db [_ args]]
    (println "argss" args)
    (let [MACHINE_ID (first (get args :MachineLoad))
         NEXT_STATE (last (get args :MachineLoad))]
      (assoc-in db [:STATE_MACHINES MACHINE_ID :LOCAL_STATE_VARS :CURRENT_STATE] NEXT_STATE))))

(reg-event-db
  :assocIN-DB-EvHandler
  (fn [db [_ args]]
    (let [MACHINE_ID (get args 0)
          VAR_ID (get args 1)
          newValue (get args 2)]
      (assoc-in db [:STATE_MACHINES MACHINE_ID :LOCAL_STATE_VARS VAR_ID] newValue))))

(reg-event-db
  ;:universalSetterEvHandler
  :updateIN-DB-EvHandler
  (fn [db [_ args]]
    (let [MACHINE_ID  (get args 0)
          VAR_ID      (get args 1)
          newOperator (get args 2)]
      (if (= newOperator :not)
          (update-in db [:STATE_MACHINES MACHINE_ID :LOCAL_STATE_VARS VAR_ID] not)
          (if (= newOperator :dec)
            (update-in db [:STATE_MACHINES MACHINE_ID :LOCAL_STATE_VARS VAR_ID] dec)
            (println "Please create a new case for anything different from :not and :dec for :updataeIN-DB-EvHandler"))))))