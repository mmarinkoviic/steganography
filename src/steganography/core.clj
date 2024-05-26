(ns steganography.core
  (:require [steganography.logics :as logics]))

(defn menu
  "Prints menu."
  []
  (println "")
  (println "Welcome to Steganography Tool!")
  (println "Choose an option:")
  (println "1. Encode message into image")
  (println "2. Decode message from image")
  (println "3. Check if two images are the same")
  (println "0. Exit")
  (print "Enter your choice: ")
  (flush))

(defn get-user-choice
  "Gets users choice."
  []
  (read-line))

(defn prompt-user
  "Displays the given prompt to the user and reads input from the console."
  [prompt]
  (println prompt)
  (read-line))

(defn encoding
  "Enables encoding on client side."
  []
  (let [file-path (prompt-user "Enter the path of image: ")
        file-name (prompt-user "Enter the name of new image with message: ")
        key (prompt-user "Enter the key for encoding: ")
        message (prompt-user "Enter the message: ")]
    (logics/encode file-path file-name key message)))

(defn decoding
  "Enables decoding on client side."
  []
  (let [file-path (prompt-user "Enter path of image: ")
        key (prompt-user "Enter the key for decoding: ")
        decoded-message (logics/decode key file-path)]
    (if (empty? decoded-message)
      (println "Message was not found or key is noy valid.")
      (println "Decoded message: " decoded-message))))

(defn check-are-they-same
  "Enables checking if two images are the same."
  []
  (try
    (let [image1-path (prompt-user "Enter path of the first image: ")
          image2-path (prompt-user "Enter path of the second image: ")
          result (logics/compare-image image1-path image2-path)]
      (if (nil? result)
        (println "An error occurred while comparing images.")
        (if result
          (println "Images are the same.")
          (println "Images are not the same."))))
    (catch Exception e)))

(defn process-user-choice
  "Process users choice."
  [choice]
  (cond
    (= choice "1") (do
                     (encoding)
                     (menu)
                     (process-user-choice (get-user-choice)))
    (= choice "2") (do
                     (decoding)
                     (menu)
                     (process-user-choice (get-user-choice)))
    (= choice "3") (do
                     (check-are-they-same)
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

(defn -main
  "Main function."
  []
  (menu)
  (process-user-choice (get-user-choice)))