(ns steganography.image-utils
  (:import (javax.imageio ImageIO)
           (java.io File)))

(^:export defn load-image
  "Loads an image from a file and returns the image object. Returns nil if an error occurs."
  [file-path]
  (try
    (ImageIO/read (File. file-path))
    (catch Exception e
      (println "Error loading image:" (.getMessage e))
      nil)))

(defn- get-executing-directory
  "Returns the path of the current working directory."
  []
  (System/getProperty "user.dir"))

(^:export defn save-image
  "Saves an image to a file in PNG format. Returns true if saving is successful, otherwise false."
  [image-data file-name]
  (let [output-file (File. (str (get-executing-directory) "/" file-name ".png"))]
    (try
      (ImageIO/write image-data "png" output-file)
      (catch Exception e
        (println "Error saving image:" (.getMessage e))
        false))))

