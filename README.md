# Steganography

This project is a tool for practicing steganography and cryptography. Steganography is the art of hiding secret messages within ordinary files, like images. It's a bit like writing an invisible note on the back of a photo!

With this Clojure-based application, you can encode messages into image files using advanced cryptographic techniques, ensuring the security and confidentiality of your hidden information. Conversely, you can decode these messages back into their original form when needed.

Additionally, it provides functionality to compare two images, allowing you to verify the success of steganography operations and determine if a hidden message exists within an image, even in the absence of a decryption key.

The project, being a console application, lays the groundwork for future expansion and enhancement.


#The project consists of two main parts:


Logic: This contains all the basic functions for encoding, decoding, and comparing images.

Console Application: The user interface enabling users to encode, decode, and compare images.


#Features


Encoding Message into Image: Allows the user to encode a secret message into an image.

Decoding Message from Image: Extracts the secret message from an image.

Checking Image Equality: Compares two images and informs the user whether they are the same or not.
