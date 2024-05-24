(ns steganography.core
  (:require [steganography.logics :as logics]
            [clojure.java.io :as io]))

(defn menu
  "Prints menu"
  []
  (println "")
  (println "Welcome to Steganography Tool!")
  (println "Choose an option:")
  (println "1. Encode message into image")
  (println "2. Decode message from image")
  (println "0. Exit")
  (print "Enter your choice: ")
  (flush))

(defn get-user-choice
  "Gets users choice"
  []
  (read-line))

(defn prompt-user [prompt]
  (println prompt)
  (read-line))

(defn process-user-choice
  "Process users choice"
  [choice]
  (cond
    (= choice "1") (do
                     (let [file-path (prompt-user "Enter the path of image: ")
                           file-name (prompt-user "Enter the name of new image with message: ")
                           key (prompt-user "Enter the key for encoding: ")
                           message (prompt-user "Enter the message: ")]
                       (logics/encode file-path file-name key message)
                       (println "Message is encoded in image."))
                     (menu)
                     (process-user-choice (get-user-choice)))
    (= choice "2") (do
                     (let [file-path (prompt-user "Enter path of image: ")
                           key (prompt-user "Enter the key for decoding: ")
                           decoded-message (logics/decode key file-path)]
                       (if (empty? decoded-message)
                         (println "Message was not found or key is noy valid.")
                         (println "Decoded message: " decoded-message)))
                     (menu)
                     (process-user-choice (get-user-choice)))
    (= choice "0") (do
                   (println "Exiting...")
                   (System/exit 0))
    :else (do
            (println "Invalid choice. Please enter a valid option.")
            (menu)
            (process-user-choice (get-user-choice)))
    ))

(defn -main []
  (menu)
  (process-user-choice (get-user-choice)))