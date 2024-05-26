(ns steganography.logics
  (:require [steganography.image-utils :as image-utils]
            [steganography.crypto :as crypto])
  )

(defn bits
  "Generates the binary representation of given number."
  [n]
  (reverse (map #(bit-and (bit-shift-right n %) 1) (range 8))))

(defn numb
  "Converts a binary representation of number into a number."
  [bits]
  (reduce (fn [acc bit] (+ (* acc 2) bit)) 0 (map int bits)))

(defn set-last-bit
  "Sets the last bit in a list of bits with given one."
  [list-of-bits new-bit]
  (concat (take 7 list-of-bits) [new-bit]))

(defn string-to-bits
  "Encrypts and converts a string into a binary representation."
  [key message]
  (let [encrypted-message (str (crypto/encrypt-message key message) "@end;")
        encrypted-bytes (.getBytes encrypted-message "UTF-8")]
    (flatten (map bits encrypted-bytes))))

(defn get-rgb
  "Gets the RGB components of a pixel at the given coordinates in the image."
  [img cord]
  (let [[x y] cord
        clr (.getRGB img x y)]
    [(bit-and (bit-shift-right clr 16) 0xff)
     (bit-and (bit-shift-right clr 8) 0xff)
     (bit-and clr 0xff)]))

(defn set-rgb
  "Sets the RGB components of the pixel at the given coordinates based on the specified RGB value of given color."
  [image cord color]
  (let [[x y] cord [r g b] color
        c (bit-or (bit-shift-left r 16) (bit-or (bit-shift-left g 8) b))]
    (.setRGB  image x y c)))

(defn match-bits-cords
  "Matches bits with corresponding coordinates in an image."
  [bits img]
  (partition 2
             (interleave (partition 3 bits)
                         (take (/ (count bits) 3)
                               (for [x (range (.getWidth img))
                                     y (range (.getHeight img))] [x y])))))

(defn set-pixels
  "Sets the RGB values of pixels of an image based on the provided data."
  [img d]
  (doseq [[data cord] d]
    (let [color-bit (partition 2 (interleave (get-rgb img cord) data))
          color (map #(let [[n b] %]
                        (numb (set-last-bit (bits n) b))) color-bit)]
      (set-rgb img cord color))))

(defn get-pixels
  "Gets the list of RGB values of all pixels of an image."
  [img]
  (map #(get-rgb img %) (for [x (range (.getWidth img))
                               y (range (.getHeight img))] [x y])))

(defn split-last-bit
  "Splits the last bits from the given byte data."
  [data]
  (map #(last (bits %)) data))

(defn decode
  "Decodes a secret message from the image file."
  [key file-path]
  (try
    (let [img (image-utils/load-image file-path)
          to-char #(char (numb (first %)))]
      (loop [bytes (partition 8 (split-last-bit (flatten (get-pixels img))))
             msg (str)]
        (if (seq bytes)
          (let [char (to-char bytes)]
            (if (or (= char \;) (= char \:))
              (let [decrypted-msg (try
                                    (crypto/decrypt-message key (subs msg 0 (- (count msg) 4)))
                                    (catch Exception e
                                      (println "Error during decryption.")
                                      ""))]
                decrypted-msg)
              (recur (rest bytes) (str msg char))))
          "")))
    (catch Exception e
      (println "Error during decoding.")
      "")))

(defn contains-message?
  "Detects if there is a message in the image file."
  [key file-path]
  (let [decoded-msg (decode key file-path)]
    (if (and decoded-msg (not= decoded-msg ""))
      decoded-msg
      nil)))

(defn encode
  "Encodes a secret message into the image file after encrypting it."
  [file-path file-name key message]
  (try
    (let [image (image-utils/load-image file-path)
          existing-message (contains-message? key file-path)]
      (if existing-message
        (do
          (println "Existing message found:")
          (println existing-message))
        (println "No existing message found."))
      (let [data (match-bits-cords (string-to-bits key message) image)]
        (set-pixels image data)
        (image-utils/save-image image file-name)
        (println "Message encoded successfully.")))
    (catch Exception e
      (println "Error during encoding." ))))

(defn compare-image
  "Checks to see if the image structure has changed."
  [path-image1 path-image2]
  (try
    (let [image1 (image-utils/load-image path-image1)
          image2 (image-utils/load-image path-image2)
          width (.getWidth image1)
          height (.getHeight image1)
          equal (ref true)]
      (doseq [x (range width)
              y (range height)]
        (if-not (= (bit-and (.getRGB image1 x y) 0xFEFFFEFF)
                   (bit-and (.getRGB image2 x y) 0xFEFFFEFF))
          (dosync (ref-set equal false))))
      @equal)
    (catch Exception e
      (println "An error occurred while comparing images:")
      nil)))