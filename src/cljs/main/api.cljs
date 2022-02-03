(ns main.api
   (:require ["util"                     :as util]
             [main.scramble              :as ms :refer [scramble?]]
             [applied-science.js-interop :as j] ))

; based on https://stackoverflow.com/questions/27914026/find-if-a-map-contains-multiple-keys
(defn contains-many? [m & ks]
  (every? #(contains? m %) ks))

(defn isValidInput? [reqBody]
   (if (not (= (count reqBody) 2))
       {:error true :message (str "Must send only 2 args, and you provided " (count reqBody))}
       (if (not (contains-many? reqBody "string1" "string2"))
           {:error true :message (str "Keys must be only 'string1' and 'string2' only")}
           (let [firstStr  (get reqBody "string1")
                 secondStr (get reqBody "string2")]
            (if (or (not (string? firstStr))
                   (not (string? secondStr)))
               {:error true :message (str "Both inputs must be of type string")}
               (let [regex #"^[a-z]+$"
                     regexResponse1 (re-seq regex firstStr)
                     regexResponse2 (re-seq regex secondStr)]
                  (if (nil? regexResponse1)
                    {:error true :message (str "string1 must contain only lowercase letters [a-z]")}
                    (if (nil? regexResponse2)
                      {:error true :message (str "string2 must "
                                                 "contain only lowercase letters [a-z]")}
                      {:error false :message (str "Success! :)")}))))))))


(defn handler [req res]

   (if (= (j/get-in req [:method]) "POST")
       (let [reqBody      (js->clj (j/get-in req [:body]))
             inputValidationObj (isValidInput? reqBody)]

         (if (not (get inputValidationObj :error))
            (let [finalResult (scramble? (get reqBody "string1") (get reqBody "string2"))]
               (println (str "finalResult: " finalResult))
               (.json (.status res 200) (clj->js {:error  false
                                                :response finalResult})))
            (.json (.status res 200) (clj->js {:error  true
                                             :response (get inputValidationObj :message)}))))
       (.json (.status res 400) (clj->js {:error  true
                                          :response "API-ENDPOINT only accepts POST messages"}))))
