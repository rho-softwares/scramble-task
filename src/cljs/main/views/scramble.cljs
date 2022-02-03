(ns main.views.scramble
  (:require
    [re-frame.core                    :as rf    :refer [subscribe dispatch]]
    [reagent.core                     :as r     :refer [as-element]]
    [xFrameNative.common              :as xc    :refer [xfn-dispatch TachyonsStyles]]
    ["react-native"                   :as rn    :refer [View KeyboardAvoidingView]]
    ["react-native-easy-grid"         :as rneg  :refer [Col Row Grid]]
    ["react-native-safe-area-context" :as rnsac :refer [SafeAreaProvider]]
    ["react-native-responsive-screen" :as rnrs  :refer [heightPercentageToDP]] ;widthPercentageToDP
    ["react-native-elements"           :as rne  :refer [Button Text Input Tooltip]]))

(defn onchange-string1-callback [value]
  (println "onChangeText/value:" value)
  (dispatch [:assocIN-DB-EvHandler [:ScrambleMachine :string1 value]]))

(defn onchange-string2-callback [value]
  (println "onChangeText/value:" value)
  (dispatch [:assocIN-DB-EvHandler [:ScrambleMachine :string2 value]]))

(defn ScrambleScreen [props]
  (let [ScrambleVars @(subscribe [:ScrambleMachine/LOCAL_STATE_VARS])
        string1      (get-in ScrambleVars [:string1])
        string2      (get-in ScrambleVars [:string2])
        showTooltip1 (get-in ScrambleVars [:showTooltip1])
        showTooltip2 (get-in ScrambleVars [:showTooltip2])]

    [:> SafeAreaProvider
    {:style (TachyonsStyles [])}
      [:> Grid
        [:> Row
          [:> Col
            [:> View
            {:style (TachyonsStyles [:flex :itemsCenter :justifyCenter :h100 :w100 :flexColumn])}
              [:> KeyboardAvoidingView
              {:style (TachyonsStyles [:flex :itemsCenter :justifyCenter
                                       :h100 :w50 :mw5 :flexColumn])}
                [:> View
                  {:style (TachyonsStyles [:mb2 :dtFixed])}
                    [:> Input
                      {:placeholder         string1
                      :onChangeText        onchange-string1-callback
                      ;:maxLength    10
                      :label        "string1"
                      :style (TachyonsStyles [:ba :w100])
                      :containerStyle (TachyonsStyles [:w100]);{:width 200}
                      :placeholderTextColor "orange"}]]
                  [:> View
                  {:style (TachyonsStyles [:mb4 :dtFixed])}
                    [:> Input
                      {:placeholder         string2
                      :onChangeText        onchange-string2-callback
                      ;:maxLength    10
                      :label        "string2"
                      ;:inputContainerStyle
                      :style (TachyonsStyles [:ba :w100])
                      :containerStyle (TachyonsStyles [:w100])
                      :placeholderTextColor "orange"}]]

                  [:> View
                  {:style (TachyonsStyles [:dtFixed :pr2 :pl2])}
                    [:> Button
                    { :title       "Scramble?"
                      :buttonStyle {:background-color "#c89c3c"}
                      :disabled    (not (and (pos? (count string1)) (pos? (count string2))))
                      :onPress     (xfn-dispatch [:ScrambleMachine :scrambleBT props])}]]]]]]]]))


            ;[:> Tooltip
            ;  {:popover (as-element [:> Text "only lowercase letters allowed [a-z]"])
            ;    :toggleAction (if showTooltip1 "onPress" "onLongPress")
            ;    :onOpen #(dispatch [:assocIN-DB-EvHandler [:ScrambleMachine :showTooltip1
            ;                                                                false]])
            ;    :backgroundColor "#98b4fc"
            ;    :withPointer true}
            ;
            ;    [:> View
            ;    {:style (TachyonsStyles [:flex :itemsCenter :justifyCenter :h100 :dtFixed :flexColumn])}
            ;      ]]