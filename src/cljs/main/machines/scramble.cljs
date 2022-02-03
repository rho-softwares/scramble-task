(ns main.machines.scramble)

(defonce ScrambleMachine {
  :MACHINE_ID :ScrambleMachine
  :FSM_SCHEME {
    :LOCAL_STATE_VARS {
      :string1 ""
      :string2 ""
      :showTooltip1 true
      :showTooltip2 true
      :CURRENT_STATE :idle}
    :STATES {
      :idle {
        :WHEN_ENTER_STATE_ACTIONS []
        :WHEN_LEAVE_STATE_ACTIONS []
        :ON_SIGNAL {
          :scrambleBT {
            :IF_COND (fn [context] true)
            :TRUE_CASE {
              :NEXT_STATE_TARGET :idle
              :STATES_TRANSITION_ACTIONS [
                [:scramble-action]]}}}}}}})