(ns steganography.crypto
  (:require [buddy.core.crypto :as cryptol]
            [buddy.core.codecs :as codecs]
            [buddy.core.hash :as hash]
            ))

(defn sha256-key
  "Generates SHA-256 hash of the given key."
  [key]
  (let [key-bytes (codecs/to-bytes key)]
    (hash/sha256 key-bytes)
    ))

(defn generate-iv
  "Generates a random Initialization Vector (IV)."
  []
  (vec (repeatedly 16 #(bit-and 255 (rand-int 256)))))

(defn encrypt-message
  "Encrypts a message using key."
  [key message]
  (let [key-bytes (sha256-key key)
        message-bytes (.getBytes message "UTF-8")
        iv (generate-iv)
        iv-array (byte-array iv)
        encrypted-bytes (cryptol/encrypt message-bytes key-bytes iv-array)
        iv-hex (apply str (map #(format "%02X" %) iv-array))
        encrypted-hex (apply str (map #(format "%02X" %) encrypted-bytes))]
    (str iv-hex encrypted-hex)))

(defn decrypt-message
  "Decrypts a message using key and IV included in the encrypted message."
  [key encrypted-message]
  (try
    (let [key-bytes (sha256-key key)
          iv-hex (subs encrypted-message 0 32)
          ciphertext-hex (subs encrypted-message 32)
          iv-bytes (codecs/hex->bytes iv-hex)
          encrypted-bytes (codecs/hex->bytes ciphertext-hex)
          decrypted-bytes (cryptol/decrypt encrypted-bytes key-bytes iv-bytes)]
      (codecs/bytes->str decrypted-bytes))
    (catch Exception e
      (when (.getMessage e)
        (if (.getMessage e)
          (.contains (.getMessage e) "Message seems corrupt or manipulated")
          false)))))