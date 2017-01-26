(defproject aws-lambda-hello-world "0.1.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 ;; AWS
                 [com.amazonaws/aws-lambda-java-core "1.1.0"] ;; core
                 [com.amazonaws/aws-java-sdk-core "1.11.76"] ;; java-sdk
                 [com.amazonaws/aws-java-sdk-rekognition "1.11.76"] ;; rekognition
                 ;;[com.amazonaws/aws-java-sdk-lambda "1.10.76"] ;; λ
                 ]
  :java-source-paths ["src/java"]
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :aot :all)
