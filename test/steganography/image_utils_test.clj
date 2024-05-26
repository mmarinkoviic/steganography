(ns steganography.image-utils-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [steganography.image-utils :as image-utils])
  (:import (javax.imageio ImageIO)
           (java.awt.image BufferedImage)
           (java.io File)))
(fact "Testing loading image."
      (let [test-image (BufferedImage. 10 10 BufferedImage/TYPE_INT_RGB)
            test-file (File/createTempFile "test-image" ".png")]
        (try
          (ImageIO/write test-image "png" test-file)
          (let [loaded-image (image-utils/load-image (.getAbsolutePath test-file))]
            (instance? BufferedImage loaded-image))
          (finally
            (.delete test-file)))))

(fact "Testing loading image that not existing."
      (image-utils/load-image "non-existent.png") => nil)

(fact "Testing saving image."
      (let [test-image (BufferedImage. 10 10 BufferedImage/TYPE_INT_RGB)
            file-name "saved-test-image"]
        (image-utils/save-image test-image file-name) => true
        (.exists (File. (str (System/getProperty "user.dir") "/" file-name ".png"))) => true
        (.delete (File. (str (System/getProperty "user.dir") "/" file-name ".png")))))

(fact "Testing saving image with mistake."
      (let [test-image nil
            file-name "invalid-image"]
        (image-utils/save-image test-image file-name) => false))