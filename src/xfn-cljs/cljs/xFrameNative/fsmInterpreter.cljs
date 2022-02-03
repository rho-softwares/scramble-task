(ns xFrameNative.fsmInterpreter
  (:require [re-frame.core      :as rf :refer [reg-event-fx dispatch after ->interceptor]]
                                               ;dispatch after
            [cljs.pprint        :as cp :refer [pprint]]
            [clojure.spec.alpha :as s]))

(println "*************** DON'T NEVER FORGET: ********************");
(println "** Interceptors are directly related to ASPECT-ORIENTED-PROGRAMMING <3 <3 ****");

(defn assocInToDefaultProps [context newProps]
  (update-in context [:coeffects :db :DEFAULT_SIGNAL_MSG :Props]
            (fn [] newProps)))

(defn appendToCommonErrors [context newItem]
  (let [errorsPath [:coeffects :db :COMMON_FSM_OUTPUTS :errors]]
    (update-in context errorsPath
      (fn [] (conj (get-in context errorsPath) newItem)))))

(defn updateCommonTargetState [context nextState]
  (update-in context [:coeffects :db :COMMON_FSM_OUTPUTS :target-state]
            (fn [] nextState)))

(defn appendToCommonFinalDispatches [context newItemList]
  (let [finalDispatchesPath [:coeffects :db :COMMON_FSM_OUTPUTS :finalDispatchN]]
    (update-in context finalDispatchesPath
      ;(fn [] (conj (get-in context finalDispatchesPath) newItem))
      (fn [] (concat (get-in context finalDispatchesPath) newItemList)))))

(defn mergeListToCommonFinalDispatches [context newDispatchesArray]
  (let [finalDispatchesPath [:coeffects :db :COMMON_FSM_OUTPUTS :finalDispatchN]]
    (update-in context finalDispatchesPath
      (fn [] (lazy-cat (get-in context finalDispatchesPath) newDispatchesArray)))))

(def printContextInterceptor
  (->interceptor
    :id      :printcofxinterceptor
    :before  (fn [context]
               (println "\n\n\n -----------------------------------------------------------")
               (println "\n -------- PRE-DISPATCHES CONTEXT GLOBAL MAP ----------------------")
               (println "\n ------------------ CONTEXT GLOBAL MAP IS: -----------------------")
               (println (str "\n" (with-out-str (pprint context))))
               (println "\n ----------------------------------------------------------------")
               (println "\n\n\n\n\n\n")
               context)))

; Context Obj is imput
(def validateViewInputsInterceptor
  (->interceptor
    :id      :validateViewInputsInterceptor
    :before  (fn [context]
                (let [inputDispatchArgs (last (get-in context [:coeffects :event]))
                      isNotInputASet    (not (map? inputDispatchArgs) )
                      validatedInputSetContext  (if isNotInputASet
                                                    (appendToCommonErrors
                                                      context ["ERRORR: View inputs =! 2"])
                                                    context)
                      addedPropsContext  (if (contains? inputDispatchArgs
                                              :Props)
                                            (assocInToDefaultProps validatedInputSetContext (get-in inputDispatchArgs [:Props]))
                                            validatedInputSetContext)
                      validatedSchemeStateMachines (if (contains?
                                                        (get-in context [:coeffects
                                                                        :db
                                                                        ])
                                                        :STATE_MACHINES)
                                                    addedPropsContext
                                                    (appendToCommonErrors
                                                      context [(str "ERROR! " :STATE_MACHINES " not inside :db")]))
                      inputMachineID    (get-in inputDispatchArgs [:Machine-ID])
                      validatedFirstSymbolContext (if (contains?
                                                        (get-in context [:coeffects
                                                                        :db
                                                                        :STATE_MACHINES])
                                                        inputMachineID)
                                                    validatedSchemeStateMachines
                                                    (appendToCommonErrors
                                                      context [(str "ERROR! " inputMachineID " not inside :STATE_MACHINES Schema")]))
                      validateLOCAL_STATE_VARS     (if (contains?
                                                        (get-in context [:coeffects
                                                                        :db
                                                                        :STATE_MACHINES
                                                                        inputMachineID])
                                                        :LOCAL_STATE_VARS)
                                                    validatedFirstSymbolContext
                                                    (appendToCommonErrors
                                                      context [(str "ERROR! " :LOCAL_STATE_VARS " not inside :STATE_MACHINES " inputMachineID " Schema")]))
                      validateCurrentStateField     (if (contains?
                                                        (get-in context [:coeffects
                                                                        :db
                                                                        :STATE_MACHINES
                                                                        inputMachineID
                                                                        :LOCAL_STATE_VARS])
                                                        :CURRENT_STATE)
                                                    validateLOCAL_STATE_VARS
                                                    (appendToCommonErrors
                                                      context [(str "ERROR! " :CURRENT_STATE " not inside :LOCAL_STATE_VARS " :CURRENT_STATE " Schema")]))
                      validateSTATEField            (if (contains?
                                                      (get-in context [:coeffects
                                                                      :db
                                                                      :STATE_MACHINES
                                                                      inputMachineID])
                                                      :STATES)
                                                    validateCurrentStateField
                                                        (appendToCommonErrors
                                                          context [(str "ERROR! " :STATES " not inside " inputMachineID " Schema")]))
                      presentState          (get-in context [:coeffects
                                                            :db
                                                            :STATE_MACHINES
                                                            inputMachineID
                                                            :LOCAL_STATE_VARS
                                                            :CURRENT_STATE])
                      validatedPresentState (if (contains?
                                                  (get-in context [:coeffects
                                                                  :db
                                                                  :STATE_MACHINES
                                                                  inputMachineID
                                                                  :STATES])
                                                  presentState)
                                                validateSTATEField
                                                    (appendToCommonErrors
                                                      context [(str "ERROR! " presentState " not inside :STATES Schema")]))
                      validateON_SIGNAL           (if (contains?
                                                        (get-in context [:coeffects
                                                                        :db
                                                                        :STATE_MACHINES
                                                                        inputMachineID
                                                                        :STATES
                                                                        presentState])
                                                        :ON_SIGNAL)
                                                    validatedPresentState
                                                    (appendToCommonErrors
                                                      context [(str "ERROR! " :ON_SIGNAL " not inside " presentState " Schema")]))
                      inputSignal       (get-in inputDispatchArgs [:Signal-ID])
                      validatedLastSymbolContext (if (contains?
                                                        (get-in context [:coeffects
                                                                        :db
                                                                        :STATE_MACHINES
                                                                        inputMachineID
                                                                        :STATES
                                                                        presentState
                                                                        :ON_SIGNAL])
                                                        inputSignal)
                                                    validateON_SIGNAL
                                                    (appendToCommonErrors
                                                      context [(str "ERROR! " inputSignal " not inside " presentState " :ON_SIGNAL Schema")]))
                      finalContext validatedLastSymbolContext]
                        finalContext))))

; IF_COND = "CONDITIONAL LOGIC" = "GUARDED LOGIC" = Business Logic 
; Full States-Transitions and Actions MAP
(def evaluateGuardedTransitionInterceptor
  (->interceptor
    :id      :evaluateGuardedTransitionInterceptor
    :before  (fn [context]
               (if (pos? (count (get-in context [:coeffects :db :COMMON_FSM_OUTPUTS :errors])))
                context
              (let [ MACHINE_ID (get-in (last (get-in context [:coeffects :event])) [:Machine-ID])
                    INPUT_SIGMA (get-in (last (get-in context [:coeffects :event])) [:Signal-ID])
                      CURRENT_STATE (get-in context [:coeffects :db
                                                    :STATE_MACHINES MACHINE_ID
                                                    :LOCAL_STATE_VARS :CURRENT_STATE])
                      IF_COND ((get-in context [:coeffects :db :STATE_MACHINES MACHINE_ID
                                              :STATES CURRENT_STATE :ON_SIGNAL INPUT_SIGMA :IF_COND]) context)
                      BOOL_CASE                 (if IF_COND
                                                  (get-in context [:coeffects :db
                                                                  :STATE_MACHINES MACHINE_ID
                                                                  :STATES CURRENT_STATE :ON_SIGNAL INPUT_SIGMA :TRUE_CASE])
                                                  (get-in context [:coeffects :db
                                                                  :STATE_MACHINES MACHINE_ID
                                                                  :STATES CURRENT_STATE :ON_SIGNAL INPUT_SIGMA :ELSE_FALSE_CASE]))
                      ValidatedNEXT_STATE_TARGET (if (contains? BOOL_CASE :NEXT_STATE_TARGET)
                                                    context
                                                    (appendToCommonErrors context
                                                      [(str "ERROR! " :NEXT_STATE_TARGET " not inside " BOOL_CASE)]))
                      NEXT_STATE (get-in BOOL_CASE [:NEXT_STATE_TARGET])
                      ;printNextstate (println (str "NEXT_STATE " NEXT_STATE))
                      ValidateddNEXT_STATE (if (contains?
                                                  (get-in context [:coeffects :db
                                                                  :STATE_MACHINES
                                                                  MACHINE_ID
                                                                  :STATES])
                                                  NEXT_STATE)
                                                ValidatedNEXT_STATE_TARGET
                                                (appendToCommonErrors context
                                                                   [(str "ERROR! NEXT_STATE " NEXT_STATE " not inside :STATES Schema")]))
                      updatedTargetCtx (updateCommonTargetState ValidateddNEXT_STATE NEXT_STATE)
                      ;oi1 (println "CURRENT_STATE" CURRENT_STATE)
                      ;oi2 (println "NEXT_STATE" NEXT_STATE)
                      addedSetNextStateCtx (if (not= CURRENT_STATE NEXT_STATE)
                                                    (appendToCommonFinalDispatches
                                                      updatedTargetCtx
                                                      [[:SetNextStateEvHandler [MACHINE_ID
                                                                                NEXT_STATE]]])
                                                    updatedTargetCtx)
                      ;gdsgdsg (println "addedSetNextStateCtx" addedSetNextStateCtx)
                      ValidatedSTATES_TRANSITION_ACTIONS (if (contains? BOOL_CASE
                                                                  :STATES_TRANSITION_ACTIONS)
                                                      addedSetNextStateCtx
                                                      (appendToCommonErrors context
                                                                   [(str "ERROR! : STATES_TRANSITION_ACTIONS not inside :STATES Schema")]))
                      addedDefaultTransitionDispatches (appendToCommonFinalDispatches
                                                        ValidatedSTATES_TRANSITION_ACTIONS
                                                        (get-in BOOL_CASE
                                                               [:STATES_TRANSITION_ACTIONS]))
                      finalContext addedDefaultTransitionDispatches]
                    finalContext)))))

(def addActionsWhenLeaveStateInterceptor
  (->interceptor
    :id      :addActionsWhenLeaveStateInterceptor
    :before  (fn [context] ; Context Obj is imput
               ;(cljs.pprint/pprint "*** Entrou addActionsWhenLeaveStateInterceptor")
               ;(cljs.pprint/pprint context)
               ;(cljs.pprint/pprint "$$$ MONEY MONYE MONEY target-state foi")
               (if (pos? (count (get-in context [:coeffects :db :COMMON_FSM_OUTPUTS :errors])))
                  context
                  (let [ MACHINE_ID         (get-in (last (get-in context [:coeffects :event]))
                                                                       [:Machine-ID])
                        ACTUAL_STATE        (get-in context [  :coeffects
                                                              :db
                                                              :STATE_MACHINES
                                                              MACHINE_ID
                                                              :LOCAL_STATE_VARS
                                                              :CURRENT_STATE])
                        whenLeaveDispatches (get-in context [  :coeffects
                                                              :db
                                                              :STATE_MACHINES
                                                              MACHINE_ID
                                                              :STATES
                                                              ACTUAL_STATE
                                                              :WHEN_LEAVE_STATE_ACTIONS])]
                    (mergeListToCommonFinalDispatches context whenLeaveDispatches))))))

(def addActionsWhenEnterStateInterceptor
  (->interceptor
    :id      :addActionsWhenEnterStateInterceptor
    :before  (fn [context] ; Context Obj is imput
               ;(cljs.pprint/pprint "*** Entrou addActionsWhenEnterStateInterceptor")
               ; First Check for errors
               (if (pos? (count (get-in context [:coeffects :db :COMMON_FSM_OUTPUTS :errors])))
                  context
                  (let [TARGET_STATE (get-in context [ :coeffects :db :COMMON_FSM_OUTPUTS
                                                     :target-state])]
                    (if (= TARGET_STATE :emptyValue)
                      (let [printerr (println "ERROO!! por algum motivo :target-state == :emptyValue, and not to the desired next-state! Investigate")]
                        context)
                      (let [MACHINE_ID (get-in (last (get-in context [:coeffects :event]))
                                                                  [:Machine-ID])
                            whenEnterDispatches (get-in context [:coeffects
                                                                :db
                                                                :STATE_MACHINES
                                                                MACHINE_ID
                                                                :STATES
                                                                TARGET_STATE
                                                                :WHEN_ENTER_STATE_ACTIONS])]
                        (mergeListToCommonFinalDispatches context whenEnterDispatches))))))))

(defn finalErrorsCheck
  [cofx]
  (let [finalDispatchDefault  (get-in cofx [:db
                                          :COMMON_FSM_OUTPUTS
                                          :finalDispatchN])
        finalDispatchEmptyChecked (if (empty? (first finalDispatchDefault))
                                    [[:print-action
                                      "[WARN] FINAL_DISPATCH_VEIO_VAZIO! (e tudo bem :)"]]
                                    finalDispatchDefault)
        errorsList (get-in cofx [:db :COMMON_FSM_OUTPUTS :errors])
        ;pr1 (println "finalDispatchDefault" finalDispatchDefault)
        ;pr1 (println "errorsList" errorsList)
        userProps (get-in cofx [:db :DEFAULT_SIGNAL_MSG :Props])
        ;actionMap {:MachineLoad (last userArgs)
        ;           :userProps userProps}
        ;oioi (println "PROPS::" userProps)
        finalDispatchN (if (pos? (count errorsList))
                          [[:print-action {:MachineLoad (str "CYCLE ERRORS: " errorsList)
                                           :Props userProps}]]
                          (for [action finalDispatchEmptyChecked]
                            ;(conj action userProps)
                            [ (first action)
                              {:MachineLoad (last action)
                              :Props userProps}]))]
    ;(println "finalDispatchN:" finalDispatchN)
    finalDispatchN))

(reg-event-fx
  :universalMachineEvHandler
  ; ---------- interceptors ----------------------
  [ ;printContextInterceptor
    validateViewInputsInterceptor        ; FSM_Scheme Validation
    evaluateGuardedTransitionInterceptor ; FSM_Scheme Validation
    addActionsWhenLeaveStateInterceptor  ; leaves S0  ; Reactive State Changes
    addActionsWhenEnterStateInterceptor  ; enters S1 ; Reactive State Changes
    ;MaybeCleanUpContext == after ==
    ;check for errors before final dispatch-n == after == final in the Context Eternal/Perpetual Flow
  ]

  (fn [cofx [_ inputDispatchArgs]]
                            ; Considera esse finalErrorsCheck como o last Interceptor
                            ; i.e. recebe um context.cofx e retorna um cofx alterado
      (let [ finalDispatchN (finalErrorsCheck cofx) ]
          ;(println (str "finalDispatchN is: " finalDispatchN))
          {:dispatch-n finalDispatchN})))