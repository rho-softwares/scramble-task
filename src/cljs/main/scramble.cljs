(ns main.scramble)

; based on https://stackoverflow.com/questions/18619785/counting-frequency-of-characters-in-a-string-using-javascript/42636979
(defn getFrequency [inputString]
   (let [strIndexes (range (count inputString))]
      (reduce
         (fn [acc_Map i]
            (let [iChar (.charAt inputString i)]
               (if (contains? acc_Map iChar)
                (assoc acc_Map iChar (+ (get acc_Map iChar) 1))
                (assoc acc_Map iChar 1))))
         {} strIndexes)))

(defn scramble? [str1 str2]
   (let [str1Histogram (getFrequency str1)
         str2Histogram (getFrequency str2)]

      (let [str2Keys (keys str2Histogram)
            reduced  (reduce
                        (fn [errValue key]
                           (let [str1KeyQuantity (if (contains? str1Histogram key)
                                                   (get str1Histogram key)
                                                   0)
                                 str2KeyQuantity (get str2Histogram key)]
                              (if (pos? (compare str2KeyQuantity
                                             str1KeyQuantity))
                                 (+ errValue 1)
                                 errValue)))
                           0 str2Keys)]
         (not (pos? reduced)))))