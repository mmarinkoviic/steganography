(ns steganography.logics-test
  (:require [midje.sweet :refer :all]
            [steganography.logics :as logics])
  (:import (java.awt.image BufferedImage)
           (java.awt Color)))

(fact "Testing bits function."
      (logics/bits 5) => [0 0 0 0 0 1 0 1]
      (logics/bits 10) => [0 0 0 0 1 0 1 0]
      (logics/bits 255) => [1 1 1 1 1 1 1 1])

(fact "Testing numb function."
      (logics/numb [0 0 0 0 0 1 0 1]) => 5
      (logics/numb [0 0 0 0 1 0 1 0]) => 10
      (logics/numb [1 1 1 1 1 1 1 1]) => 255)

(fact "Testing set-last-bit function."
      (logics/set-last-bit [0 0 0 0 0 0 0 0] 1) => [0 0 0 0 0 0 0 1]
      (logics/set-last-bit [1 1 1 1 1 1 1 0] 0) => [1 1 1 1 1 1 1 0]
      (logics/set-last-bit [1 0 1 0 1 0 1 0] 1) => [1 0 1 0 1 0 1 1])

(fact "Testing string-to-bits function."
      (let [expected-bits (* (count "Hello, world!") 8)
            result (logics/string-to-bits "mykey" "Hello, world!")]
        (= (count result) expected-bits)))

(fact "Testing get-rgb function."
      (let [test-img (BufferedImage. 10 10 BufferedImage/TYPE_INT_RGB)
            test-color (Color. 255 0 0)
            test-cords [5 5]]
        (.setRGB test-img (first test-cords) (second test-cords) (.getRGB test-color))
        (logics/get-rgb test-img test-cords) => [255 0 0]))

(fact "Testing det-rgb function."
      (let [test-image (BufferedImage. 10 10 BufferedImage/TYPE_INT_RGB)
            test-cord [5 5]
            test-color [255 0 0]]
        (logics/set-rgb test-image test-cord test-color)
        (logics/get-rgb test-image test-cord) => [255 0 0]))

(fact "Testing get-pixels function."
      (let [test-image (BufferedImage. 2 2 BufferedImage/TYPE_INT_RGB)]
        (= (logics/get-pixels test-image) [ [0 0 0] [0 0 0] [0 0 0] [0 0 0] ])))

(fact "Testing split-last-bit function."
      (logics/split-last-bit [170 187 204]) => '(0 1 0)
      (logics/split-last-bit [255 128 64]) => '(1 0 0))

(fact "Testing decoding message from image."
      (logics/decode "mykey" (.getPath (clojure.java.io/resource "Image1.png"))) => "Good morning!")

(fact "Testing decoding message from image when the key is wrong."
      (logics/decode "wrongkey" (.getPath (clojure.java.io/resource "Image1.png"))) => false)

(fact "Testing decoding message from image when the path is wrong."
      (logics/decode "mykey" "wrong-path/test-image.png") => "")

(fact "Testing containing message when message exists."
      (with-redefs [logics/decode (fn [_ _] "Hello, world!")]
        (logics/contains-message? "mykey" "test-image.png")) => "Hello, world!")

(fact "Testing containing message when message does not exist."
      (with-redefs [logics/decode (fn [_ _] "")]
        (logics/contains-message? "mykey" "test-image.png")) => nil)

(fact "Testing containing message when the key is wrong."
      (with-redefs [logics/decode (fn [_ _] "")]
        (logics/contains-message? "wrongkey" "test-image.png")) => nil)

(fact "Testing encoding message into image."
      (with-redefs [logics/contains-message? (fn [k fp] nil)
                    logics/match-bits-cords (fn [bits img] '[[[0 0] [0 1] [0 2]]])
                    logics/set-pixels (fn [_ _] nil)]
        (logics/encode "test-path" "test-image.png" "mykey" "Hello, world!")) => nil)

(fact "Testing encoding message into the image when there are not previous message."
      (with-redefs [logics/contains-message? (fn [k fp] "Existing message")
                    logics/match-bits-cords (fn [bits img] '[[[0 0] [0 1] [0 2]]])
                    logics/set-pixels (fn [_ _] nil)]
        (logics/encode "test-path" "test-image.png" "mykey" "Hello, world!")) => nil)

(fact "Testing compare-image function with two identical images."
      (logics/compare-image
        (.getPath (clojure.java.io/resource "Image1.png"))
        (.getPath (clojure.java.io/resource "Image1.png"))) => true)

(fact "Testing compare-image function with two different images."
      (logics/compare-image
        (.getPath (clojure.java.io/resource "Image1.png"))
        (.getPath (clojure.java.io/resource "Image2.png"))) => false)