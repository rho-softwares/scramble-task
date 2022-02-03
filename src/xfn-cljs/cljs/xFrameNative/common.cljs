(ns xFrameNative.common
(:require [re-frame.core              :as rf  :refer [dispatch]]
            ["tachyons-react-native"    :as trn]
            [xFrameNative.handlersDB    :as xhd :refer [_loadMachinesToAppDB]]
            [applied-science.js-interop :as j]))

;------ Exported Code to View+FSM_SCHEME use ------------------------------------------------

(defn preAppendZero [userInput]
  (if (> (count (str userInput)) 1)
    userInput
    (str "0" userInput)))

(defn getCLockStr [Clock]
  (let [hour               (.getHours Clock)
        minutes            (.getMinutes Clock)
        seconds            (.getSeconds Clock)
        preAppendedHours   (preAppendZero hour)
        preAppendedMinutes (preAppendZero minutes)
        preAppendedSeconds (preAppendZero seconds)]
  (str preAppendedHours ":" preAppendedMinutes ":" preAppendedSeconds)))

(defn TachyonsStyles [args]
  ;; original React/Reagent Styling: [:> Text {:style {:color "white" :fontSize 80}} "myText"]
  (map #(j/get trn/styles %) args)
)

(defonce TachyonsThemes {

  :PrimaryDarkMode
  {:View            [:flex :itemsCenter
                      :justifyCenter :bgBlack80]
    :SimplestView    [:bgBlack80 :h100 :w100]
    :SimpleButton    [:b :f5 :pa2 :bgBlack40 :brPill :mb3]
    :SimpleButton2   [:b :f5 :pa2 :bgBlack40 :brPill :selfCenter]
    :SimpleButtonTxt [:b :pl3 :pr3 :f5 :orange]
    :SimpleH1Txt     [:b :pl3 :pr3 :f2 :orange]
    :SimpleSmallTxt  [:b :f5 :orange]
    :TextInput       [:h2 :w4 :ma3 :ba :bgWhite90]
    :BottomTabs      "gold"
  }

  :Primary
  {:View            [:flex :itemsCenter
                      :justifyCenter :bgGreen]
    :SimplestView    [:bgGreen :h100 :w100]
    :SimpleButton    [:b :f5 :pa2 :bgPurple :brPill :mb3]
    :SimpleButton2   [:b :f5 :pa2 :bgPurple :brPill :selfCenter]
    :SimpleButtonTxt [:b :pl3 :pr3 :f5 :black]
    :SimpleH1Txt     [:b :pl3 :pr3 :f2 :black]
    :SimpleSmallTxt  [:b :f5 :black]
    :textInput       [:h2 :w4 :ma3 :ba :bgWhite90]
    :BottomTabs      "#873e23"
  }
})

(defn tachyons [selectedTheme]
  (TachyonsStyles (get-in TachyonsThemes selectedTheme)))

(defn universalMachineDispatch [args]
  (dispatch [:universalMachineEvHandler args]))

(defn loadMachinesToAppDB [args]
  (_loadMachinesToAppDB args))

(defn xfn-dispatch
  [args]
  (let [ MachineIdentifier (first args)
         SignalIdentifier  (second args)
         Props  (last args)]
    ;(println "Props:" Props)
    #(universalMachineDispatch {:Machine-ID MachineIdentifier
                                :Signal-ID  SignalIdentifier
                                :Props      Props})))

(defn xfn-styles [args]
  (let [is_dark_mode (first args)
        comp-id      (last args)]
    (if is_dark_mode
      (tachyons [:PrimaryDarkMode comp-id])
      (tachyons [:Primary         comp-id]))))