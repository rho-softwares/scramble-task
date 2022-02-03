(ns main.app
  (:require ["@react-navigation/native"           :as rnnav   :refer [NavigationContainer]]
            ["@react-navigation/stack"            :as rnnavs  :refer [createStackNavigator]]
            ["@react-navigation/bottom-tabs"      :as rnnavbt :refer [createBottomTabNavigator]]
            ["@react-navigation/drawer"           :as rnd     :refer [createDrawerNavigator]]
            [reagent.core                         :as r       :refer [reactify-component]]
            [xFrameNative.common                  :as xfc     :refer [loadMachinesToAppDB]]
            [main.machines.scramble               :as mms     :refer [ScrambleMachine]]
            [main.views.scramble                  :as mvs     :refer [ScrambleScreen]]))

;------- React-Navigation Stuff ----------------z-------------------------------------
(defonce StackNavigator (.-Navigator (createStackNavigator)))
(defonce Screen         (.-Screen (createStackNavigator)))
(defonce Tab            (createBottomTabNavigator))
(defonce Stack          (createStackNavigator))
(defonce Drawer         (createDrawerNavigator))

(defn app-root []
  [:> NavigationContainer
    [:> (.-Navigator Drawer)
    {:initialRouteName "HelloWorld" :screenOptions {:headerShown false :gestureEnabled false}}
      [:> (.-Screen Drawer)     { :name "Scramble"
                                  :component (reactify-component ScrambleScreen)}]]])

(loadMachinesToAppDB [ScrambleMachine])