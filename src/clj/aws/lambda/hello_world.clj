(ns aws.lambda.hello-world
  (:require [clojure.data.json :as json]
            [clojure.string :as s]
            [clojure.java.io :as io]
            [aws.lambda.rekognition :as rekognition])
  (:gen-class
   :init init
   :constructors {[] []}   
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))


(defn key->keyword [key-string]
  (-> key-string
      (s/replace #"([a-z])([A-Z])" "$1-$2")
      (s/replace #"([A-Z]+)([A-Z])" "$1-$2")
      (s/lower-case)
      (keyword)))

(defn -init
  ;; matches empty constructor
  ([][[] (do (println "Lambda Setup."))]))

(defn create-collection [request]
  (prn "CREATE COLLECTION CALLED")
  (let [collection-name (get-in request [:data :collection-name])
        response (rekognition/createCollection collection-name)]
    {:code 201
     :response response}
    ))


(defn insert-image-collection [request]
  (prn "INSERT IMAGE CALLED")
  (let [collection-name (get-in request [:data :collection-name])
        image-str (get-in request [:data :image])
        response (rekognition/indexFaceRecords collection-name image-str)]
    {:code 201
     :response response}
    ))

(defn insert-multi-images-collection [request]
  (prn "MULTI IMAGE INSERT")
  (let [collection-name (get-in request [:data :collection-name])
        images (get-in request [:data :images])
        response-mid (map (partial rekognition/indexFaceRecords collection-name) images)
        response (into [] (flatten response-mid))]
    (prn response)
    {:code 201
     :response response}))

(defn list-in-collection [request]
  (prn "LIST IN COLLECTION CALLED")
  (let [collection-name (get-in request [:data :collection-name])
        response (rekognition/listIndexesInCollection collection-name)]
    {:code 200
     :response response}
    ))

(defn delete-image-collection [request]
  "Returns array of faces or empty array if nil"
  (prn "DELETE IMAGE CALLED")
  (let [collection-name (get-in request [:data :collection-name])
        image-arr-str (get-in request [:data :image])
        response (rekognition/deleteFaces collection-name image-arr-str)]
    {:code 202
     :response response}))

(defn similarity-image-collection [request]
  (prn "CHECK SIMILARITY BETWEEN IMAGES")
  (let [collection-name (get-in request [:data :collection-name])
        image-str (get-in request [:data :image])
        response (rekognition/similarity-list collection-name image-str 10)
        ]
    {:code 200
     :response response}))

(defn get-face-detail [request]
  (prn "GET FACE DETAIL")
  (let [image-str (get-in request [:data :image])
        response (rekognition/getFaceDetail image-str)]
    {:code 200
     :response response}))

(defn delete-collection [request]
  (prn "DELETE COLLECTION CALLED")
  (let [collection-name (get-in request [:data :collection-name])
        response (rekognition/deleteCollection collection-name)]
    {:code response
     :response (get-in request [:data :collection-name])}
    ))

(defn handle-request [request-map]
  (let [sz-input (prn-str request-map)]
  (do
    (println "Process Request" request-map))
  (case (:operation request-map)
    "ruok" {:code 200
        :response "imok"}
    "create-collection" (create-collection request-map)
    "delete-collection" (delete-collection request-map)
    "insert-in-collection" (insert-image-collection request-map)
    "multi-insert-in-collection" (insert-multi-images-collection request-map)
    "list-in-collection" (list-in-collection request-map)
    "delete-in-collection" (delete-image-collection request-map)
    "similarity-in-collection" (similarity-image-collection request-map)
    "get-face-detail" (get-face-detail request-map)

    {:code 500
     :response "Please send proper operation"})

  ))


;; implements interface RequestStreamHandler
;; [see](https://github.com/aws/aws-lambda-java-libs/blob/master/aws-lambda-java-core/src/main/java/com/amazonaws/services/lambda/runtime/RequestStreamHandler.java)
(defn -handleRequest [this is os context]
  (let [w (io/writer os)]
    (->
     ;; parse input to clojure map
     (json/read (io/reader is) :key-fn key->keyword)
     ;; pass request to handle-request
     (handle-request)
     ;; pass return of handle-request to json writer
     (json/write w))
    ;; flush output stream
    (.flush w)))

;; (handle-request {:operation "create-collection"})