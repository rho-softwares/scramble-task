(ns main.effects
(:require [re-frame.core             :as rf   :refer [reg-fx reg-event-fx subscribe
                                                      dispatch dispatch-sync]]
          [applied-science.js-interop :as j]
          ;["util"                     :as util]
          ["axios"                    :as axios]))

(reg-fx
  :scramble-fx
  (fn [args]
    (let []
      ;(println (str "args: " args))
      (.catch
        (.then
          (.post axios
            ;"https://flexiana-test.vercel.app/api/cors-api"
            ;"http://localhost:3000/api/cors-api"
            "https://scramble-task.vercel.app/api/cors-api"
            (clj->js {:string1 (first args) :string2 (last args)}))

          (fn [response]
            ;(.inspect util (j/get-in response ["data"]))
            (let [respData (j/get-in response ["data"])
                  isErr    (j/get-in respData ["error"])
                  alertMsg (if isErr
                               (str "[ERROR] " (j/get-in respData ["response"]))
                               (if (j/get-in respData ["response"])
                                   "[RESULT] Success! A portion of string1 can be rearranged to match string2 😊"
                                   "[RESULT] No portion of string1 can be rearranged to match string2 😔 "))]
              (js/alert alertMsg))))

        (fn [err]
            (js/alert err)) ))))