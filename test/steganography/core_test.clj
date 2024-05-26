(ns steganography.core-test
  (:require [midje.sweet :refer :all]
            [steganography.core :as core]
            [steganography.logics :as logics]))

(fact "Testing function that prints the correct menu options"
      (with-out-str (core/menu)) => "\r\nWelcome to Steganography Tool!\r\nChoose an option:\r\n1. Encode message into image\r\n2. Decode message from image\r\n3. Check if two images are the same\r\n0. Exit\r\nEnter your choice: ")

(fact "Testing get-user-choice function"
      (with-in-str "1\n"
                   (core/get-user-choice)) => "1")

(fact "Testing get-user-choice function."
      (with-in-str "2\n"
                   (core/get-user-choice)) => "2")

(fact "Testing check-are-they-same function when images are the same."
      (with-redefs [core/prompt-user (fn [prompt] (if (= prompt "Enter path of the first image: ")
                                               "path/to/image1.png"
                                               "path/to/image2.png"))
                    logics/compare-image (fn [path1 path2] true)
                    println (fn [s] s)]
        (core/check-are-they-same) => "Images are the same."))

(fact "Testing check-are-they-same function when images are not the same."
      (with-redefs [core/prompt-user (fn [prompt] (if (= prompt "Enter path of the first image: ")
                                               "path/to/image1.png"
                                               "path/to/image2.png"))
                    logics/compare-image (fn [path1 path2] false)
                    println (fn [s] s)]
        (core/check-are-they-same) => "Images are not the same."))
