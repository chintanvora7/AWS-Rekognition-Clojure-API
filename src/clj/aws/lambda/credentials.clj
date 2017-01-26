(ns aws.lambda.credentials
  (:import [com.amazonaws.auth BasicAWSCredentials BasicSessionCredentials]
           [com.amazonaws.auth.profile ProfileCredentialsProvider]))

(defn profile-credentials [profile]
  (.getCredentials (ProfileCredentialsProvider. profile)))

(defn basic-credentials [accessKey secretKey]
  (BasicAWSCredentials. accessKey secretKey))

(defn basic-session [accessKey secretKey sessionToken]
  (BasicSessionCredentials. accessKey secretKey sessionToken))
