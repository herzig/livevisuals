(ns quilblub.core
  (:require [quil.core :as q]
            [quil.middleware :as qm]
            [clojure.core.matrix :as m])
  (:import [java.awt GraphicsEnvironment Robot Rectangle image.BufferedImage]
           [processing.core PImage])
  
  (:use overtone.core)
  ;(:require '[clojure.core.matrix :as ccm])
  )

;; (use 'overtone.core)
(connect-external-server 57110)

;; Define a synth we can use to tap into the stereo input
(defsynth tapper
  "Tap into a stereo bus. Provides 3 taps: left, right, and phase."
  [bus 0 freq 40]
  (let [source (sound-in bus 2)
        left (select 0 source)
        right (select 1 source)]
    (tap :left freq left)
    (tap :right freq right)
    )
  )

(comment 
  (def au (:taps (tapper 0)))
  @(:left au)
  @(:right au))

(defn grab-screen [x y h w]
  (let [ge (GraphicsEnvironment/getLocalGraphicsEnvironment)
        gs (.getScreenDevices ge)
        bounds (Rectangle. h w x y)]
    (.createScreenCapture (Robot. (aget gs 0)) bounds)))

(defn setup []
  (q/frame-rate 15)                   ;; Set framerate to 1 FPS
  (q/background 200))

((def TW 40)
 (def TH 60)
 (def qw q/width)
 (def qh q/height)
)

(defn draw []

  (q/blend-mode  :screen)
   ;(q/push-matrix)

  (q/push-matrix)
  (q/translate (/ (qw) 2) (/ (qh) 2))
  ;(q/rotate (q/radians (rand-int 10)))
  ;(q/rotate (q/radians (* 100 @(:left au))))
  (q/translate (/ (qw) -2) (/ (qh) -2))


  ;(println (q/width))
  (let [img (PImage. (grab-screen (qw) (qh) 0 20))]
    ;(q/set-image 0 0 img)

    ;(q/background 0 0 255)
    ;(q/blend-mode :multiply)
    (q/image img 0 0))

  (q/pop-matrix)

  (q/blend-mode  :difference)

  (let [xpos #(min (rand-int (qw)) (- (qw) TW))
        ypos #(min (rand-int (qh)) (- (qh) TH))]
    (dotimes [i 5] (apply q/copy (repeatedly 2 #(concat [(xpos)] [0] [TW (qh)]))))
    (dotimes [i 5] (apply q/copy (repeatedly 2 #(concat [0] [(ypos)] [(qw) TH])))))

   (let [xpos #(min (* (q/random 0.5 1.5) (qw) (q/noise (* 50 @(:left au)))) (- (qw) TW))
         ypos #(min (* (q/random 1 1.5) (qh) (q/noise (* 50 @(:right au)))) (- (qh) TH))]  
    (dotimes [i 5] (apply q/copy (repeatedly 2 #(concat [(xpos)] [0] [TW (qh)]))))
    (dotimes [i 10] (apply q/copy (repeatedly 2 #(concat [0] [(ypos)] [(qw) TH]))))
    )


  
  (q/blend-mode :screen)
  (q/fill 100 128 128 128)
  (q/ellipse 400 600 (* @(:left au) 3000) (* @(:right au) 1000))
  (q/ellipse 1200 100 (* @(:right au) 2500) (* @(:left au) 1000))

  (q/ellipse 100 200 (* @(:right au) 1000) (* @(:left au) 1000))
  (q/ellipse 600 300 (* @(:right au) 2500) (* @(:left au) 2005))


  ;(q/background 255)
  
  (q/fill 128 255 128 20)
   (q/begin-shape)
   (q/push-matrix)
   (q/translate 600 300)
   (doseq [a (range -0.6 (* 4 Math/PI) 0.5)]
     (let [r (q/noise (* a @(:left au) 1000))]
       (apply q/curve-vertex (map (partial * r 300) [(Math/cos a) (Math/sin a)]))
        ;; (apply q/ellipse (map (partial * r 300) [(Math/cos a) (Math/sin a) 0.05 0.05])))
       ))
   (q/end-shape)
   (q/pop-matrix)
  


  (q/fill 128 50 128 100)
  (q/begin-shape)
  (q/push-matrix)
  (q/translate 500 800)
  (doseq [a (range -0.6 (* 4 Math/PI) 0.5)]
    (let [r (q/noise (* a @(:left au) 1000))]
      (apply q/curve-vertex (map (partial * r 500) [(Math/cos a) (Math/sin a)]))
      ;; (apply q/ellipse (map (partial * r 300) [(Math/cos a) (Math/sin a) 0.05 0.05])))
      ))
  (q/end-shape)
  (q/pop-matrix)
  )
  ;(q/background 0)

  ;(let [px (q/pixels)
  ;      reds (map q/red px)]
  ;  (println (count reds))
    ;(println  (count px))
    ;(println ccm/matrix px)


(comment
  (* @(:left mytaps) 255)
  )

(q/defsketch sketchy                  ;; Define a new sketch named example
  :title "SGMK HOME MADE"    ;; Set the title of the sketch
  :settings #(q/smooth 0)             ;; Turn on anti-aliasing
  :setup setup                        ;; Specify the setup fn
  :features [:resizable] 
  :draw draw                          ;; Specify the draw fn
  :size [1280 800])                    ;; You struggle to beat the golden ratio