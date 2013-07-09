(defproject clj-thrift "0.1.0-SNAPSHOT"
  :description "A Clojure abstraction for Thrift"
  :url "https://github.com/dcuddeback/clj-thrift"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}

  :min-lein-version "2.0.0"

  :source-paths ["src/clojure"]

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.incubator "0.1.3"]
                 [org.apache.thrift/libthrift "0.9.0"]]


  :profiles {:1.3 {:dependencies [[org.clojure/clojure "1.3.0"]]}
             :1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :1.6 {:dependencies [[org.clojure/clojure "1.6.0-master-SNAPSHOT"]]}

             :thrift0.8 {:dependencies [[org.apache.thrift/libthrift "0.8.0"]]}
             :thrift0.9 {:dependencies [[org.apache.thrift/libthrift "0.9.0"]]}

             :provided {:dependencies [[org.slf4j/slf4j-log4j12 "1.5.2"]]}

             :dev {:dependencies [[midje "1.5.1"]]
                   :plugins [[lein-thriftc "0.1.0"]
                             [lein-midje "3.0.1"]]
                   :prep-tasks ["thriftc" "javac" "compile"]}

             :lint {:global-vars {*warn-on-reflection* true}}}

  :aliases {"lint" ["with-profile" "+lint" "midje"]}

  ; Exclude the compiled Thrift classes from the Jar file.
  :jar-exclusions [#"clj_thrift/fakes"]

  :repositories {"sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail :update :always}}
                 "sonatype-snapshots" {:url "http://oss.sonatype.org/content/repositories/snapshots"
                                       :snapshots true
                                       :releases {:checksum :fail :update :always}}}

  :deploy-repositories [["releases" {:url "https://clojars.org/repo" :username :gpg :password :gpg}]
                        ["snapshots" {:url "https://clojars.org/repo" :username :gpg :password :gpg}]])
