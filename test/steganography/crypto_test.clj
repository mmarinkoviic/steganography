(ns steganography.crypto-test
  (:require [midje.sweet :refer :all]
            [steganography.crypto :as crypto]))

(fact "Testing generating SHA-256 hash of thr given key."
      (crypto/sha256-key "mykey") => #(instance? (Class/forName "[B") %))

(fact "Testing generating SHA-256 hash of thr given key."
      (let [result (crypto/sha256-key "mykey")]
        (count result) => 32))

(fact "Testing generating a random Initialization Vector (IV)."
      (crypto/generate-iv) => #(and (vector? %)
                                    (= 16 (count %))))

(fact "Testing encrypting message."
      (let [key "mykey"
            message "This is a secret message."
            encrypted-message (crypto/encrypt-message key message)]
        (count encrypted-message) => #(> % 32)))

(fact "Testing decrypting message."
      (let [key "mykey"
            message "This is a secret message."
            encrypted-message (crypto/encrypt-message key message)]
        (crypto/decrypt-message key encrypted-message) => message))

(fact "Testing decrypting message with wrong key."
      (let [key "mykey"
            wrong-key "wrongkey"
            message "This is a secret message."
            encrypted-message (crypto/encrypt-message key message)]
        (crypto/decrypt-message wrong-key encrypted-message) => false))

