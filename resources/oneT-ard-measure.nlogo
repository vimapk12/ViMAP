;;--;;--;;--;;--;;--;;--;;
;  
;   Copyright 2014  
;   Mind, Matter & Media Lab, Vanderbilt University.
;   This is a source file for the ViMAP open source project.
;   Principal Investigator: Pratim Sengupta 
;   Lead Developer: Mason Wright
;  
;   Simulations powered by NetLogo. 
;   The copyright information for NetLogo can be found here: 
;   https://ccl.northwestern.edu/netlogo/docs/copyright.html  
;   
;;--;;--;;--;;--;;--;;--;; 


;ARDUINO ADDITION START
extensions [arddue]
;ARDUINO ADDITION END

breed [wabbits wabbit]
breed [blocks block]
breed [sensors sensor]
breed [agent-kinds kind]
breed [args arg]

breed [ measurepoints measurepoint ]
measurepoints-own [
 tticks
 tcycles
 theading
 todometer
 tdistfromlast
 tspeed
 taccel
 tcolor
 parent
]

wabbits-own [pen-was-down ;; if true, the pen was down before the wabbit wrapped around the screen. Only used for wrapping around (so you can
                  ;; put the pen back down after the turtle wraps around, if and only if it was down before.
             agent-type ;; "yellow-ball", "blue-ball", or "red-ball"
             flag-counter ;; counts the number of flags/markers that have been dropped so far in the run
             bonus-speed ;; how much faster than initial speed a wabbit is moving; can be negative, but starts at 0
             initial-x
             initial-y
             previous-x
             previous-y
            ]
agent-kinds-own 
[
  name
  methods-list
  primitives-list
]
args-own
[
  arg-type
  default-value
  max-value
  min-value
  enum-list
]
blocks-own  
[
  block-name ; the name that will appear on the block
  arg-list
  is-observer

  ; the name of the block's category, from categories-list (if any)
  category
]

sensors-own
[
  sensor-name
  sensor-type
]

globals
   [
    future-x ;; tells what the wabbit's x-coordinate would be if it went forward the given distance
    serial-port
    number-of-steps
    color-for-draw
    
    first-flag
    second-flag
    flag-distance
    
    ;;NEEDED FOR MEASURE LINKING
    measure-points
    
    wabbits-list
    wabbit-type-list
    blocks-list ;; list of blocks used to populate the toolbars / palettes in Java construction-world
    sensors-list ;; list of sensors for Java construction-world
    agent-type-list ;; list of agent-type in the model (breeds)
    predicate-list
    chart-data-name-list ;; list of data types to report to Java for graphing after each cycle
    speed-up-amount
    speed-down-amount
    fd-step-size   

    ; list of category names for blocks
    categories-list
        
    measure-option-string-lists
    measure-option-command-list
    
    var-name-list
    
    can-highlight-agents
    last-cycle
   ]

;;;;;;;;;;;;;;;;;;;;;
;ARDUINO ADDITION START
to startup
  setup-arduino
end

to setup-arduino
  set serial-port user-one-of "Select a port:" arddue:ports
  arddue:open serial-port
end

to-report arduino-distance
  let real-dist (item 0 arddue:digital)
  ;if (real-dist > 10) [ set real-dist 10 ]
  report precision real-dist 2
end


;ARDUINO ADDITION END
;;;;;;;;;;;;;;;;;;;;;

to setup ;; sets up the screen
  clear-all
  set-defaults
  color-background-patches
  make-other-stuff
  create-blocks-list
  create-sensors-list
  create-agent-type-list
  create-predicate-list
  create-chart-data-name-list
  create-categories-list
  create-measure-option-string-lists
  create-measure-option-command-list
  create-var-name-list
end

to setup-cycle
end

to takedown-cycle
end

to create-var-name-list
  set var-name-list
  [
    "the turtle's heading" 
    "the turtle's odometer" 
    "the distance from last measure-point" 
    "the turtle's speed" 
    "change in distance covered"
  ]
end

to create-measure-option-string-lists
  set measure-option-string-lists []
  
  let tlist []
  set tlist lput "Odometer" tlist
  set tlist lput "Reading" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists
  
  set tlist []
  set tlist lput "Distance Covered" tlist
  set tlist lput "since" tlist
  set tlist lput "Last Measure" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists
  
  set tlist []
  set tlist lput "Change in Distance Covered" tlist 
  set tlist lput "since" tlist
  set tlist lput "Last Measure" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists
end

to create-measure-option-command-list
  set measure-option-command-list []
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 5 graph" measure-option-command-list
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 6 graph" measure-option-command-list
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 8 graph" measure-option-command-list
end

to-report get-list-as-csv [list-name]
  let retn ""
  foreach list-name
  [
   set retn (word retn ? "," )
  ]
  report butlast retn
end

to-report center-radius-and-name
;report "10,10,10"

  let result-string ""
  let cpoint nobody
  let cpoint2 nobody
  let crad nobody
  
  user-message "Click and drag to define center and radius..."
  
  while [not mouse-down?] [ ]
  set result-string (word (precision mouse-xcor 2)"," (precision mouse-ycor 2))
  
  create-turtles 1 [ set shape "circle" set color black set size 5 setxy mouse-xcor mouse-ycor set cpoint self ]
  create-turtles 1 [ set shape "circle" set color red set size 1 setxy mouse-xcor mouse-ycor set cpoint2 self ]
  create-turtles 1 [ set shape "line half" set crad self setxy mouse-xcor mouse-ycor set color black]
  
  display
  let radius 0
  let d 0
  while [mouse-down?]
  [
    ask cpoint [ set d distancexy mouse-xcor mouse-ycor ]
    ask crad [ set size 2 * d  if (mouse-xcor != xcor or mouse-ycor != ycor) [ set heading towardsxy mouse-xcor mouse-ycor]  ]
 	ask cpoint2 [ set size 2 * d ]
 	display
  ]
  
  set result-string ( word result-string "," (precision d 2) )
  ask crad [ die ]
  ask cpoint [ die ]
  ask cpoint2 [ die ]
  
  
  let my-name ""
  while [length my-name = 0 ] 
  [ set my-name user-input ("What is the name of this set of turtles?") ]
  
  
  set result-string ( word result-string "," my-name )
  report result-string
end

to create-chart-data-name-list
  set chart-data-name-list ["distance-from-start" "distance-from-previous"]
end

to-report distance-from-start [ current-agent-type ]
  let result 0
  
  ifelse not any? wabbits with [ agent-type = current-agent-type ]
  [set result " "]
  [
    ask wabbits with [ agent-type = current-agent-type ]
    [
      set result result + distancexy initial-x initial-y
      set result result / (count wabbits with [ agent-type = current-agent-type] )
      set result precision result 1
    ]
  ]
  
  report result
end

to-report distance-from-previous [current-agent-type]
  let result 0
  
  ifelse not any? wabbits with [ agent-type = current-agent-type ]
  [set result " "]
  [
    ask wabbits with [ agent-type = current-agent-type ]
    [
      ifelse previous-x = "" or previous-y = ""
      [set result " "]
      [
        set result result + distancexy previous-x previous-y
        set result result / (count wabbits with [ agent-type = current-agent-type] )
        set result precision result 1
      ]
    ]
  ]
  
  report result
end

to cycle-ended
  ask wabbits
  [
    set previous-x xcor
    set previous-y ycor
  ]
end

to create-categories-list
  set categories-list
  [
    "Control"
    "Movement"
    "Drawing"
    "Sensors"
  ]
end

to create-predicate-list
  set predicate-list ["true" "false" "pcolor-blue"]
end

to-report java-true [aWho]
  report true
end

to-report java-false [aWho]
  report false
end

to-report java-is-using-repeat
  report true
end

to-report java-is-image-computation
  report false
end

to-report java-pcolor-blue [aWho]
let result ""
ask turtle aWho
[set result [pcolor] of patch-here = blue]
 report result
end

to reset
setup
end

to create-sensors-list
  set sensors-list []

  ;;;;; END OF SENSOR DEFINITIONS ;;;;;

end

to create-blocks-list
  set blocks-list []
  
  ;;;;; PUT BLOCK DEFINITIONS HERE: ;;;;;
  
  ;;;; declare arduino blocks here
  create-blocks 1
  [
    set block-name "move-ard"
    set category "Movement"
    set arg-list []
    set is-observer false
    ; other variables not applicable
  ]

      set blocks-list lput max-one-of blocks [who] blocks-list
 
 create-blocks 1
  [
    set block-name "speed-up-ard"
    set category "Movement"
    set arg-list []
    set is-observer false
    ; other variables not applicable
  ]

      set blocks-list lput max-one-of blocks [who] blocks-list

create-blocks 1
  [
    set block-name "slow-down-ard"
    set category "Movement"
    set arg-list []
    set is-observer false
    ; other variables not applicable
  ]

      set blocks-list lput max-one-of blocks [who] blocks-list

create-blocks 1
  [
    set block-name "right-turn-ard"
    set category "Movement"
    set arg-list []
    set is-observer false
    ; other variables not applicable
  ]

      set blocks-list lput max-one-of blocks [who] blocks-list

create-blocks 1
  [
    set block-name "left-turn-ard"
    set category "Movement"
    set arg-list []
    set is-observer false
    ; other variables not applicable
  ]

      set blocks-list lput max-one-of blocks [who] blocks-list
 
  ;;;;; end arduino block ;;;;;;;
  
  create-blocks 1
  [
    set block-name "change"
    set category "Control"
    set arg-list []
    hatch-args 1
    [
      set arg-type "enum"
      set enum-list ["forward" "color" "heading"] ;"speed-up-amount"]; "pen-thickness"]
    ]
    set arg-list lput max-one-of args [who] arg-list
    hatch-args 1
    [
      set arg-type "enum"
      set enum-list ["plus" "minus" "times" "divide"]
    ]
    set arg-list lput max-one-of args [who] arg-list
	hatch-args 1
	[
	   set arg-type "int"
	   set default-value 1
	   set max-value 50
	   set min-value 0
	]
	set arg-list lput max-one-of args [who] arg-list
	set is-observer false
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  
  create-blocks 1
  [
    set block-name "set-pen-thickness-by"
    set category "Drawing"
    set arg-list []
    hatch-args 1
    [
      set arg-type "enum"
      set enum-list ["speed-down-number" "speed-up-number" "forward-dist"]
    ]    
      set arg-list lput max-one-of args [who] arg-list
      set is-observer false
  ]
    set blocks-list lput max-one-of blocks [who] blocks-list

create-blocks 1
  [
    set block-name "set-color-by-hand"
    set category "Drawing"
    set arg-list []
    hatch-args 1
    [
      set arg-type "enum"
      set enum-list ["dsensor" "four-times-dsensor" "ten-times-dsensor"]
    ]    
      set arg-list lput max-one-of args [who] arg-list
      set is-observer false
  ]
    set blocks-list lput max-one-of blocks [who] blocks-list
  
  create-blocks 1
  [
    set block-name "set-heading"
        set category "Movement"
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 1
      set max-value 360
      set min-value 1
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    ; other variables not applicable
  ]
    set blocks-list lput max-one-of blocks [who] blocks-list

  create-blocks 1
  [
    set block-name "set-pen-thickness"
    set category "Drawing"
    
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 1
      set max-value 50
      set min-value 1
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    ; other variables not applicable
  ]
    set blocks-list lput max-one-of blocks [who] blocks-list
  
  create-blocks 1
  [
    set block-name "forward"
    set category "Movement"

    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 1
      set max-value 100
      set min-value 0
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
  ]
    set blocks-list lput max-one-of blocks [who] blocks-list
    create-blocks 1
  [
    set block-name "speed-up"
    set category "Movement"
    
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 1
      set max-value 50
      set min-value 1
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    ; other variables not applicable
  ]
    set blocks-list lput max-one-of blocks [who] blocks-list
    create-blocks 1
  [
    set block-name "slow-down"
    set category "Movement"
    
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 1
      set max-value 50
      set min-value 1
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    ; other variables not applicable
  ]
    set blocks-list lput max-one-of blocks [who] blocks-list
      create-blocks 1
  [
    set block-name "right"
    set category "Movement"
    
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 90
      set max-value 180
      set min-value 1
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      create-blocks 1
  [
    set block-name "left"
    set category "Movement"
    
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 90
      set max-value 180
      set min-value 1
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
  create-blocks 1
  [
    set block-name "pen-up"
    set category "Drawing"
    set arg-list []
    set is-observer false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      create-blocks 1
  [
    set block-name "pen-down"
    set category "Drawing"
    set arg-list []
    set is-observer false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
        create-blocks 1
  [
    set block-name "plant-flag"
    set category "Drawing"
    set arg-list []
    set is-observer false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
  
  ;;; Isaac's Blocks
  
  create-blocks 1
  [
    set block-name "turn-some-patches-blue"
    set category "Drawing"
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 10
      set max-value 200
      set min-value 1
    ]
    set arg-list lput max-one-of args [who] arg-list
      set is-observer false
    ; other variables not applicable
    
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  
  create-blocks 1
  [
    set block-name "color-circle-blue"
    set category "Drawing"
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 10
      set max-value 10000
      set min-value 1 
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
     ; other variables not applicable
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  
  create-blocks 1
  [
    set block-name "color-square-blue"
    set category "Drawing"
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 100
      set max-value 20000    
      set min-value 1
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
     ; other variables not applicable
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  
  create-blocks 1
  [
    set block-name "color-polygon"
    set category "Drawing"
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 4
      set max-value 1000
      set min-value 3
    ]
    set arg-list lput max-one-of args [who] arg-list
    hatch-args 1
    [
      set arg-type "enum"
      set default-value "blue"
      set enum-list ["blue" "red" "yellow" "green" "brown" "black" "white"]
    ]
    set arg-list lput max-one-of args [who] arg-list 
      
    set is-observer false
      ; other variables not applicable
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
    
    
  create-blocks 1
  [
   set block-name "create-ngon"
   set category "Drawing"
   set arg-list []
   hatch-args 1 ; sides
   [
    set arg-type "int"
    set default-value 3
    set max-value 100
    set min-value 3
   ]
   set arg-list lput max-one-of args [who] arg-list
   hatch-args 1 ; radius
   [
    set arg-type "int"
    set default-value 30
    set max-value 150
    set min-value 1
   ]
   set arg-list lput max-one-of args [who] arg-list
   
   hatch-args 1 ; color
   [
    set arg-type "enum"
    set default-value "blue"
	set enum-list [ "blue" "red" "green" ]
   ]
   set arg-list lput max-one-of args [who] arg-list
   
   set is-observer false
  ]
    set blocks-list lput max-one-of blocks [who] blocks-list
      
;  create-blocks 1
;  [
;      set block-name "turn-all-patches blue"  
;  ]
; 
;  create-blocks 1
;  [
;      set block-name "turn-turtles-blue"
;  ]
;  
;  create-blocks 1 
;  [
;    set block-name "move-forward-1"
;  ]
; 
  ;;;;; END OF BLOCK DEFINITIONS ;;;;;
end

to create-agent-type-list
  set agent-type-list [] 
  
  create-agent-kinds 1
  [
    set name "turtle"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []
    set primitives-list lput "change" primitives-list
    set primitives-list lput "forward" primitives-list
    set primitives-list lput "speed-up" primitives-list
    set primitives-list lput "slow-down" primitives-list
    set primitives-list lput "pen-up" primitives-list
    set primitives-list lput "pen-down" primitives-list
    set primitives-list lput "right" primitives-list
    set primitives-list lput "left" primitives-list
    set primitives-list lput "plant-flag" primitives-list
    set primitives-list lput "set-pen-thickness-by" primitives-list
     set primitives-list lput "set-heading" primitives-list
     set primitives-list lput "set-pen-thickness" primitives-list
    set primitives-list lput "move-ard" primitives-list
    set primitives-list lput "speed-up-ard" primitives-list
    set primitives-list lput "set-color-by-hand" primitives-list
    set primitives-list lput "slow-down-ard" primitives-list    
     set primitives-list lput "right-turn-ard" primitives-list 
     set primitives-list lput "left-turn-ard" primitives-list
    set primitives-list lput "create-ngon" primitives-list
  ]
  set agent-type-list lput max-one-of agent-kinds [who] agent-type-list
end

to-report speed-down-number
  report speed-down-amount
end

to-report speed-up-number
  report speed-up-amount
end

to-report forward-dist
  report fd-step-size
end

to-report dsensor
  report arduino-distance
end

to-report four-times-dsensor
  report 4 * arduino-distance
end

to-report ten-times-dsensor
  report 10 * arduino-distance
end

to create-wabbits-list
  set wabbits-list sort [who] of wabbits
end

to create-wabbit-type-list
  create-wabbits-list
  set wabbit-type-list []
  
  foreach wabbits-list
  [
    set wabbit-type-list lput [agent-type] of turtle ? wabbit-type-list
  ]
end

to java-change [variable-name operator-name change-value]
  if variable-name = "forward"
  [
    if operator-name = "plus"
    [set bonus-speed bonus-speed + change-value]
    if operator-name = "minus"
    [set bonus-speed bonus-speed - change-value]
    if operator-name = "times"
    [set bonus-speed bonus-speed * change-value]
    if operator-name = "divide" and change-value != 0
    [set bonus-speed bonus-speed / change-value]
  ]
  if variable-name = "color"
  [
    if operator-name = "plus"
    [set color color + change-value]
    if operator-name = "minus"
    [set color color - change-value]
    if operator-name = "times"
    [set color color * change-value]
    if operator-name = "divide" and change-value != 0
    [set color color / change-value]
  ]
    if variable-name = "heading"
  [
    if operator-name = "plus"
    [set heading heading + change-value]
    if operator-name = "minus"
    [set heading heading - change-value]
    if operator-name = "times"
    [set heading heading * change-value]
    if operator-name = "divide" and change-value != 0
    [set heading heading / change-value]
  ]
  
     if variable-name = "speed-up-amount"
  [
    if operator-name = "plus"
    [set bonus-speed bonus-speed + change-value]
    if operator-name = "minus"
    [set bonus-speed bonus-speed - change-value]
    if operator-name = "times"
    [set bonus-speed bonus-speed * change-value]
    if operator-name = "divide" and change-value != 0
    [set bonus-speed bonus-speed / change-value]
  ]
  if variable-name = "pen-thickness"
  [
    if operator-name = "plus"
    [set pen-size pen-size + change-value]
    if operator-name = "minus"
    [set pen-size pen-size - change-value]
    if operator-name = "times"
    [set pen-size pen-size * change-value]
    if operator-name = "divide" and change-value != 0
    [set pen-size pen-size / change-value]
  ]
end

to java-forward [ distance-number]
let forward-distance distance-number + bonus-speed
  if forward-distance < 0
  [set forward-distance 0]
  forward forward-distance
  set fd-step-size forward-distance
end

to java-speed-up [ amount-number]
set bonus-speed bonus-speed + amount-number
set speed-up-amount amount-number
end

to java-slow-down [ amount-number]
set bonus-speed bonus-speed - amount-number
end

to java-right [ amount-number]
right amount-number
end

to java-left [ amount-number]
left amount-number
end

to java-set-heading [ amount-number]
set heading amount-number
end

to java-set-pen-thickness [ amount-number]
set pen-size amount-number
end

to java-pen-up
pen-up
end

to java-pen-down
pen-down
end

to java-move-ard
  set fd-step-size arduino-distance
  fd arduino-distance
  ;set fd-step-size arduino-distance
end

to java-speed-up-ard
;  set fd-step-size arduino-distance
;  fd arduino-distance
  set bonus-speed bonus-speed + arduino-distance
  set speed-up-amount arduino-distance
  ;set fd-step-size arduino-distance
end

to java-slow-down-ard
;  set fd-step-size arduino-distance
;  fd arduino-distance
  set bonus-speed bonus-speed - arduino-distance
;  set speed-up-amount arduino-distance
  ;set fd-step-size arduino-distance
end

to java-right-turn-ard
  rt arduino-distance
  ;set fd-step-size arduino-distance
end

to java-left-turn-ard
  lt arduino-distance
  ;set fd-step-size arduino-distance
end

to java-plant-flag
let temp-flag-count 0
  let x 0
  let y 0
  let color-to-draw 0
  
  set temp-flag-count flag-counter
  set x xcor
  set y ycor
  set color-to-draw color
  
  let remain temp-flag-count mod 100
  let remain-ones remain mod 10
  let remain-tens ( remain - remain-ones ) / 10
     
  draw-plus x y color-to-draw

  set y (y + 10)
  draw-digit remain-ones x y color-to-draw

  set x ( x - 6 )
  draw-digit remain-tens x y color-to-draw

  set flag-counter flag-counter + 1
end

to java-turn-some-patches-blue [ amount-number]
  ask n-of amount-number patches [ set pcolor blue ]
end

to java-set-pen-thickness-by [amount-number]
  set pen-size runresult(amount-number)
end

to java-set-color-by-hand [amount-number]
  set color runresult(amount-number)
end




;to java-turn-all-patches-blue
;  ask patches [set pcolor blue]
;end

;to java-turn-turtles-blue
;  ask turtles [set color blue]
;end

;to java-move-forward-one
;  forward 1 
;end

to java-color-circle-blue [ amount-number]
ask patches in-radius amount-number [set pcolor blue]
end

to java-color-square-blue [ side-length] 
    let y ycor
    let x xcor 
    let amount-value (side-length / 2)
    ask patches with [ 
      (((pxcor >= x - amount-value) and (pxcor <= x)) or ((pxcor <= x + amount-value) and (pxcor >= x))) ; defines the x-coordinates of the included (right and left) patches relative to the turles x-coordinate   
      and                                                                                                ; combines both - four possibilites 
      (((pycor >= y - amount-value) and (pycor <= y)) or ((pycor <= y + amount-value) and (pycor >= y)))]; defines the y-coordinates of the included (up and down) patches relative to the turles y-coordinate   
    [set pcolor blue] 
end

patches-own [isaac-variable-a ;assign to row fill-ins
            isaac-variable-b] ; assign to column fill-ins

to java-color-polygon [ number-of-sides b-color ] 
   let color-of-polygon runresult b-color 
   rt 180
   forward ((6 / 2) / (tan ((360 / number-of-sides) / 2))) ;get turtle out of center into half-segment of side length
   rt 90 
   repeat (6 / 2) ; move forward along the side
   [
     set pcolor color-of-polygon
     set isaac-variable-a 1
     forward 1
   ]
   rt 180 - ((180 - (360 / number-of-sides)) / 1) ;do so for other sides 
   repeat (number-of-sides - 1) 
 [ 
  repeat 6
  [
    set pcolor color-of-polygon
    set isaac-variable-a 1 
    forward 1 
  ]
  rt 180 - ((180 - (360 / number-of-sides)) / 1)
 ]
  repeat (6 / 2) ;move along first side to where turtle started
  [
    set pcolor color-of-polygon
    set isaac-variable-a 1
    forward 1
  ]
 
  rt 90
  forward ((6 / 2) / (tan ((360 / number-of-sides) / 2))) ; go back to center
  let x xcor
  let y ycor
  ask patches with [isaac-variable-a = 1] 
  [
    let py pycor
    let px pxcor
    ask patches with ; fill in by rows with perimter as reference 
    [
      ((pycor = py) and ((pxcor >= px) and (pxcor <= x)))
      or
      ((pycor = py) and ((pxcor <= px) and (pxcor >= x)))
    ]
    [
      set isaac-variable-a 2
    ]    
   ask patches with ; fill in by columns with perimeter as reference 
    [
      ((pxcor = px) and ((pycor >= py) and (pycor <= y)))
      or
      ((pxcor = px) and ((pycor <= py) and (pycor >= y)))
    ]
    [
      set isaac-variable-b 3
    ]
   ]
  ask patches with [(isaac-variable-a = 2) and (isaac-variable-b = 3)] [set pcolor color-of-polygon] ;intersection of rows and columns to remove extraneous fill in
  set isaac-variable-a 0
  set isaac-variable-b 0
end  

to java-create-ngon [ sides radius aColor ]
  let cX xcor
  let cY ycor
  
  ; these will hold the coefficients of condition functions
  let xIsLess [] ; vertical line at left bound, xcor only
  let xIsGreater[] ; vertical line at right bound, xcor only
  let yIsLessSlope[] ; non-vertical line to be below, slope only. matches same index in yIsLessIntercept
  let yIsLessIntercept[] ; non-vertical line to be below, y-intercept only
  let yIsGreaterSlope[] ; non-vertical line to be above, slope only. matches same index in yIsGreaterIntercept
  let yIsGreaterIntercept[] ; non-vertical line to be above, y-intercept only
  
  let i 0
  while [ i < sides ]
  [
   ; angle from center of polygon to vertex. initially 90, or straight ahead
   let theta1 ( 90 - 360 / sides * i ) mod 360 
   
   ; coordinates of first of two neighboring vertices
   let x1 cX + radius * cos theta1
   let y1 cY + radius * sin theta1
   
   let j i + 1
   ; wrap around to 0
   if j >= sides
   [ set j 0 ]
   ; angle to the next neighbor vertex from center
   let theta2 ( 90 - 360 / sides * j ) mod 360

   ; coordinates of next neighbor vertex, proceeding around the circumscribed circle
   let x2 cX + radius * cos theta2
   let y2 cY + radius * sin theta2
   
   ; if the line is vertical, handle as a special case
   ifelse x1 = x2
   [
     ; if must be below the line
     ifelse cX < x1
     [
       set xIsLess lput x1 xIsLess
     ]
     ; else must be above the line
     [
       set xIsGreater lput x1 xIsGreater
     ]
   ]
   ; else must not be a vertical line, so slope exists
   [
     let slope ( y2 - y1 ) / ( x2 - x1 )
     let intercept -1 * x1 * slope + y1
     ; if must be above the line
     ifelse cY > cX * slope + intercept
     [
       set yIsGreaterSlope lput slope yIsGreaterSlope
       set yIsGreaterIntercept lput intercept yIsGreaterIntercept
     ]
     ; else must be below the line
     [
       set yIsLessSlope lput slope yIsLessSlope
       set yIsLessIntercept lput intercept yIsLessIntercept
     ]
   ]
   
   ; proceed to next vertex. will run once for each edge of the polygon, making it into a constraint
   set i i + 1
  ]
  
let theColor runresult aColor
ask patches with [ pxcor <= max-pxcor ]
[
  ; patch is "valid" if it satisfies all n constraints
  let is-valid true
  
  ; is the patch x less than all vertical line right bounds?
  foreach xIsLess
  [
    if pxcor >= ?
    [ set is-valid false ]
  ]
  ; is the patch x greater than all vertical line left bounds?
  foreach xIsGreater
  [
    if pxcor <= ?
    [ set is-valid false ]
  ]
  ; is the patch's y below all non-vertical line upper bounds?
  set i 0
  while [ i < length yIsLessSlope ]
  [
    if pycor >= pxcor * item i yIsLessSlope + item i yIsLessIntercept
    [ set is-valid false ]
    set i i + 1
  ]
  ; is the patch's y above all non-vertical line lower bounds?
  set i 0
  while [ i < length yIsGreaterSlope ]
  [
    if pycor <= pxcor * item i yIsGreaterSlope + item i yIsGreaterIntercept
    [ set is-valid false ]
    set i i + 1
  ]
  
  ; if the patch satisfies all n constraints, color it blue
  if is-valid
  [ set pcolor theColor ]
]
end

to draw-plus [x y color-to-draw]
  ask patch x y [set pcolor color-to-draw]
  ask patch (x + 1) y [set pcolor color-to-draw]
  ask patch (x - 1) y [set pcolor color-to-draw]
  ask patch x (y + 1) [set pcolor color-to-draw]
  ask patch x (y - 1) [set pcolor color-to-draw]
  ask patch (x + 2) y [set pcolor color-to-draw]
  ask patch (x - 2) y [set pcolor color-to-draw]
  ask patch x (y + 2) [set pcolor color-to-draw]
  ask patch x (y - 2) [set pcolor color-to-draw]
end

to draw-digit [digit x y color-to-draw]
   if digit = 1
                       [draw-one x y color-to-draw]
                 if digit = 2
                         [draw-two x y color-to-draw]
                 if digit = 3
                         [draw-three x y color-to-draw]
                 if digit = 4
                         [draw-four x y color-to-draw]
                 if digit = 5
                         [draw-five x y color-to-draw]
                 if digit = 6
                         [draw-six x y color-to-draw]
                 if digit = 7
                         [draw-seven x y color-to-draw]
                 if digit = 8
                         [draw-eight x y color-to-draw]
                 if digit = 9
                         [draw-nine x y color-to-draw]
                 if digit = 0
                         [draw-zero x y color-to-draw]
end

to draw-one [x-for-draw y-for-draw  color-to-draw]
  let x x-for-draw + 3
  let y y-for-draw
  let a  color-to-draw
  ask patch x y [set pcolor a]
  ask patch x (y + 1) [set pcolor a]
  ask patch x (y - 1) [set pcolor a]
  ask patch x (y + 2) [set pcolor a]
  ask patch x (y - 2) [set pcolor a]
  ask patch x (y + 3) [set pcolor a]
  ask patch x (y - 3) [set pcolor a]
  ask patch x (y + 4) [set pcolor a]
  ask patch x (y - 4) [set pcolor a]
end

to draw-two [x-for-draw y-for-draw  color-to-draw]
  let x x-for-draw
  let y y-for-draw
  let a  color-to-draw
  ask patch x (y + 4) [set pcolor a]
  ask patch (x + 1) (y + 4) [set pcolor a]
  ask patch (x + 2) (y + 4) [set pcolor a]
  ask patch (x + 3) (y + 4) [set pcolor a]
  ask patch (x + 4) (y + 4) [set pcolor a]
  ask patch (x + 4) (y + 3) [set pcolor a]
  ask patch (x + 4) (y + 2) [set pcolor a]
  ask patch (x + 4) (y + 1) [set pcolor a]
  ask patch (x + 4) y [set pcolor a]
  ask patch (x + 3) y [set pcolor a]
  ask patch (x + 2) y [set pcolor a]
  ask patch (x + 1) y [set pcolor a]        
  ask patch x y [set pcolor a]
  ask patch x (y - 1) [set pcolor a]
  ask patch x (y - 2) [set pcolor a]
  ask patch x (y - 3) [set pcolor a]
  ask patch x (y - 4) [set pcolor a]
  ask patch (x + 1) (y - 4) [set pcolor a]
  ask patch (x + 2) (y - 4) [set pcolor a]
  ask patch (x + 3) (y - 4) [set pcolor a]
  ask patch (x + 4) (y - 4) [set pcolor a]
end

to draw-three [x-for-draw y-for-draw  color-to-draw]
  let x x-for-draw
  let y y-for-draw
  let a  color-to-draw
  ask patch x (y + 4) [set pcolor a]
  ask patch (x + 1) (y + 4) [set pcolor a]
  ask patch (x + 2) (y + 4) [set pcolor a]
  ask patch (x + 3) (y + 4) [set pcolor a]
         
  ask patch x y [set pcolor a]
  ask patch (x + 1) y [set pcolor a]
  ask patch (x + 2) y [set pcolor a]
  ask patch (x + 3) y [set pcolor a]
         
  ask patch x (y - 4) [set pcolor a]
  ask patch (x + 1) (y - 4) [set pcolor a]
  ask patch (x + 2) (y - 4) [set pcolor a]
  ask patch (x + 3) (y - 4) [set pcolor a]
  
  ask patch (x + 4) (y + 4) [set pcolor a]
  ask patch (x + 4) (y + 3) [set pcolor a]
  ask patch (x + 4) (y + 2) [set pcolor a]
  ask patch (x + 4) (y + 1) [set pcolor a]
  ask patch (x + 4) y [set pcolor a]
  ask patch (x + 4) (y - 1) [set pcolor a]
  ask patch (x + 4) (y - 2) [set pcolor a]
  ask patch (x + 4) (y - 3) [set pcolor a]
  ask patch (x + 4) (y - 4) [set pcolor a]
end

to draw-four [x-for-draw y-for-draw  color-to-draw]
  let x x-for-draw
  let y y-for-draw
  let a  color-to-draw
  ask patch x (y + 4) [set pcolor a]
  ask patch x (y + 3) [set pcolor a]
  ask patch x (y + 2) [set pcolor a]
  ask patch x (y + 1) [set pcolor a]
         
  ask patch x y [set pcolor a]
  ask patch (x + 1) y [set pcolor a]
  ask patch (x + 2) y [set pcolor a]
  ask patch (x + 3) y [set pcolor a]
         
  ask patch (x + 4) (y + 4) [set pcolor a]
  ask patch (x + 4) (y + 3) [set pcolor a]
  ask patch (x + 4) (y + 2) [set pcolor a]
  ask patch (x + 4) (y + 1) [set pcolor a]
  ask patch (x + 4) y [set pcolor a]
  ask patch (x + 4) (y - 1) [set pcolor a]
  ask patch (x + 4) (y - 2) [set pcolor a]
  ask patch (x + 4) (y - 3) [set pcolor a]
  ask patch (x + 4) (y - 4) [set pcolor a]
end

to draw-five [x-for-draw y-for-draw  color-to-draw]
  let x x-for-draw
  let y y-for-draw
  let a  color-to-draw
  ask patch x (y + 4) [set pcolor a]
  ask patch (x + 1) (y + 4) [set pcolor a]
  ask patch (x + 2) (y + 4) [set pcolor a]
  ask patch (x + 3) (y + 4) [set pcolor a]
  ask patch (x + 4) (y + 4) [set pcolor a]
         
  ask patch x y [set pcolor a]
  ask patch (x + 1) y [set pcolor a]
  ask patch (x + 2) y [set pcolor a]
  ask patch (x + 3) y [set pcolor a]
  ask patch (x + 4) y [set pcolor a]
         
  ask patch x (y - 4) [set pcolor a]
  ask patch (x + 1) (y - 4) [set pcolor a]
  ask patch (x + 2) (y - 4) [set pcolor a]
  ask patch (x + 3) (y - 4) [set pcolor a]
  ask patch (x + 4) (y - 4) [set pcolor a]
         
  ask patch x (y + 4) [set pcolor a]
  ask patch x (y + 3) [set pcolor a]
  ask patch x (y + 2) [set pcolor a]
  ask patch x (y + 1) [set pcolor a]
         
  ask patch (x + 4) (y - 1) [set pcolor a]
  ask patch (x + 4) (y - 2) [set pcolor a]
  ask patch (x + 4) (y - 3) [set pcolor a]
  ask patch (x + 4) (y - 4) [set pcolor a]
end

to draw-six [x-for-draw y-for-draw  color-to-draw]
  let x x-for-draw
  let y y-for-draw
  let a  color-to-draw
  ask patch x (y + 4) [set pcolor a]
  ask patch (x + 1) (y + 4) [set pcolor a]
  ask patch (x + 2) (y + 4) [set pcolor a]
  ask patch (x + 3) (y + 4) [set pcolor a]
  ask patch (x + 4) (y + 4) [set pcolor a]
         
  ask patch x y [set pcolor a]
  ask patch (x + 1) y [set pcolor a]
  ask patch (x + 2) y [set pcolor a]
  ask patch (x + 3) y [set pcolor a]
  ask patch (x + 4) y [set pcolor a]
         
  ask patch x (y - 4) [set pcolor a]
  ask patch (x + 1) (y - 4) [set pcolor a]
  ask patch (x + 2) (y - 4) [set pcolor a]
  ask patch (x + 3) (y - 4) [set pcolor a]
         
  ask patch x (y + 4) [set pcolor a]
  ask patch x (y + 3) [set pcolor a]
  ask patch x (y + 2) [set pcolor a]
  ask patch x (y + 1) [set pcolor a]
  ask patch x (y - 1) [set pcolor a]
  ask patch x (y - 2) [set pcolor a]
  ask patch x (y - 3) [set pcolor a]
  ask patch x (y - 4) [set pcolor a]
         
  ask patch (x + 4) (y - 1) [set pcolor a]
  ask patch (x + 4) (y - 2) [set pcolor a]
  ask patch (x + 4) (y - 3) [set pcolor a]
  ask patch (x + 4) (y - 4) [set pcolor a]
end

to draw-seven [x-for-draw y-for-draw  color-to-draw]
  let x x-for-draw
  let y y-for-draw
  let a  color-to-draw
  ask patch x (y + 4) [set pcolor a]
  ask patch (x + 1) (y + 4) [set pcolor a]
  ask patch (x + 2) (y + 4) [set pcolor a]
  ask patch (x + 3) (y + 4) [set pcolor a]
  ask patch (x + 4) (y + 4) [set pcolor a]
         
  ask patch (x + 4) (y + 4) [set pcolor a]
  ask patch (x + 4) (y + 3) [set pcolor a]
  ask patch (x + 4) (y + 2) [set pcolor a]
  ask patch (x + 4) (y + 1) [set pcolor a]
  ask patch (x + 4) y [set pcolor a]
  ask patch (x + 4) (y - 1) [set pcolor a]
  ask patch (x + 4) (y - 2) [set pcolor a]
  ask patch (x + 4) (y - 3) [set pcolor a]
  ask patch (x + 4) (y - 4) [set pcolor a]
end

to draw-eight [x-for-draw y-for-draw  color-to-draw]
  let x x-for-draw
  let y y-for-draw
  let a  color-to-draw
  ask patch x (y + 4) [set pcolor a]
  ask patch (x + 1) (y + 4) [set pcolor a]
  ask patch (x + 2) (y + 4) [set pcolor a]
  ask patch (x + 3) (y + 4) [set pcolor a]
         
  ask patch x y [set pcolor a]
  ask patch (x + 1) y [set pcolor a]
  ask patch (x + 2) y [set pcolor a]
  ask patch (x + 3) y [set pcolor a]
         
  ask patch x (y - 4) [set pcolor a]
  ask patch (x + 1) (y - 4) [set pcolor a]
  ask patch (x + 2) (y - 4) [set pcolor a]
  ask patch (x + 3) (y - 4) [set pcolor a]
         
  ask patch (x + 4) (y + 4) [set pcolor a]
  ask patch (x + 4) (y + 3) [set pcolor a]
  ask patch (x + 4) (y + 2) [set pcolor a]
  ask patch (x + 4) (y + 1) [set pcolor a]
  ask patch (x + 4) y [set pcolor a]
  ask patch (x + 4) (y - 1) [set pcolor a]
  ask patch (x + 4) (y - 2) [set pcolor a]
  ask patch (x + 4) (y - 3) [set pcolor a]
  ask patch (x + 4) (y - 4) [set pcolor a]
           
  ask patch x (y + 4) [set pcolor a]
  ask patch x (y + 3) [set pcolor a]
  ask patch x (y + 2) [set pcolor a]
  ask patch x (y + 1) [set pcolor a]
  ask patch x y [set pcolor a]
  ask patch x (y - 1) [set pcolor a]
  ask patch x (y - 2) [set pcolor a]
  ask patch x (y - 3) [set pcolor a]
  ask patch x (y - 4) [set pcolor a]
end

to draw-nine [x-for-draw y-for-draw  color-to-draw]
  let x x-for-draw
  let y y-for-draw
  let a  color-to-draw
  ask patch x (y + 4) [set pcolor a]
  ask patch (x + 1) (y + 4) [set pcolor a]
  ask patch (x + 2) (y + 4) [set pcolor a]
  ask patch (x + 3) (y + 4) [set pcolor a]
         
  ask patch x y [set pcolor a]
  ask patch (x + 1) y [set pcolor a]
  ask patch (x + 2) y [set pcolor a]
  ask patch (x + 3) y [set pcolor a]
         
  ask patch (x + 4) (y + 4) [set pcolor a]
  ask patch (x + 4) (y + 3) [set pcolor a]
  ask patch (x + 4) (y + 2) [set pcolor a]
  ask patch (x + 4) (y + 1) [set pcolor a]
  ask patch (x + 4) y [set pcolor a]
  ask patch (x + 4) (y - 1) [set pcolor a]
  ask patch (x + 4) (y - 2) [set pcolor a]
  ask patch (x + 4) (y - 3) [set pcolor a]
  ask patch (x + 4) (y - 4) [set pcolor a]
         
  ask patch x (y + 4) [set pcolor a]
  ask patch x (y + 3) [set pcolor a]
  ask patch x (y + 2) [set pcolor a]
  ask patch x (y + 1) [set pcolor a]
  ask patch x y [set pcolor a]  
end

to draw-zero [x-for-draw y-for-draw  color-to-draw]
  let x x-for-draw
  let y y-for-draw
  let a  color-to-draw
  ask patch x (y + 4) [set pcolor a]
  ask patch (x + 1) (y + 4) [set pcolor a]
  ask patch (x + 2) (y + 4) [set pcolor a]
  ask patch (x + 3) (y + 4) [set pcolor a]
  
  ask patch x (y - 4) [set pcolor a]
  ask patch (x + 1) (y - 4) [set pcolor a]
  ask patch (x + 2) (y - 4) [set pcolor a]
  ask patch (x + 3) (y - 4) [set pcolor a]
  
  ask patch (x + 4) (y + 4) [set pcolor a]
  ask patch (x + 4) (y + 3) [set pcolor a]
  ask patch (x + 4) (y + 2) [set pcolor a]
  ask patch (x + 4) (y + 1) [set pcolor a]
  ask patch (x + 4) y [set pcolor a]
  ask patch (x + 4) (y - 1) [set pcolor a]
  ask patch (x + 4) (y - 2) [set pcolor a]
  ask patch (x + 4) (y - 3) [set pcolor a]
  ask patch (x + 4) (y - 4) [set pcolor a]
         
  ask patch x (y + 4) [set pcolor a]
  ask patch x (y + 3) [set pcolor a]
  ask patch x (y + 2) [set pcolor a]
  ask patch x (y + 1) [set pcolor a]
  ask patch x y [set pcolor a]
  ask patch x (y - 1) [set pcolor a]
  ask patch x (y - 2) [set pcolor a]
  ask patch x (y - 3) [set pcolor a]
  ask patch x (y - 4) [set pcolor a]
end

to-report turtle-property [who-number property]
  let result [runresult property] of turtle who-number
  report result
end

to-report primitive-list-length [agent-name]
  let current-agent-kind one-of agent-kinds with [name = agent-name]
  let result 0
  ask current-agent-kind
  [set result length primitives-list]
  report result
end

to-report primitive-list-item [agent-name item-number]
  let current-agent-kind one-of agent-kinds with [name = agent-name]
  let result ""
  ask current-agent-kind
  [set result item item-number primitives-list]
  report result
end

to-report method-list-length [agent-name]
  let current-agent-kind one-of agent-kinds with [name = agent-name]
  let result 0
  ask current-agent-kind
  [set result length methods-list]
  report result
end

to-report method-list-item [agent-name item-number]
  let current-agent-kind one-of agent-kinds with [name = agent-name]
  let result ""
  ask current-agent-kind
  [set result item item-number methods-list]
  report result
end

to-report list-length [list-name]
  report length list-name
end


to-report arg-list-length [block-index]
  report length [ arg-list ] of item block-index blocks-list
end

to-report property-of-arg-for-block [arg-index block-index property]
  report [runresult property] of [item arg-index arg-list] of item block-index blocks-list
end

to-report list-item [list-name index]
  report item index list-name
end

to-report list-item-property [list-name index property]
  report [runresult property] of item index list-name ; runresult converts the String variable into its NetLogo code equivalent (a property name)
end

to set-defaults
  set-default-shape wabbits "circle"
  
  set number-of-steps 0
  set can-highlight-agents false
  set last-cycle false
end

to color-background-patches
  ask patches
       [set pcolor white]
end

to make-other-stuff
  create-wabbits 1
       [setxy  200 0
        set heading 0
        set color green
        set shape "turtle"
        set agent-type "turtle"
       ]
  ask wabbits
       [set size 30
        set pen-size 3
        set pen-was-down false ;; i.e., pen is up now
        set bonus-speed 0
        set flag-counter 0
        set initial-x xcor
        set initial-y ycor
        set previous-x ""
        set previous-y ""
       ]
end

to-report get-measures
  let result []
  foreach measure-points 
  [
    ask ? 
    [ 
      if (is-string? tdistfromlast) 
      [ set tdistfromlast 0 ]
    
      let datarep (list who tcolor (word "\"" [agent-type] of parent "\"") tcycles theading todometer tdistfromlast tspeed taccel) 
      set result lput datarep result 
    ]
  ]
  report result
end

to-report get-measures-for [an-agent-type]
  let result []
  let relevant-measures measurepoints with [ [agent-type] of parent = an-agent-type ]
  let relevant-list sort relevant-measures
  foreach relevant-list 
  [
    ask ? 
    [ 
      if (is-string? tdistfromlast) 
      [ set tdistfromlast 0 ]
      
      let datarep (list who tcolor (word "\"" [agent-type] of parent "\"") tcycles theading todometer tdistfromlast tspeed taccel) 
      set result lput datarep result 
    ]
  ]
  report result
end

to-report get-agent-types-as-csv
  let retn ""
  foreach agent-type-list
  [
   set retn (word retn ([name] of ?) "," )
  ]
  report butlast retn
end

to-report java-is-repeat-var
  report false
end

@#$#@#$#@
GRAPHICS-WINDOW
10
10
421
442
-1
200
1.0
1
10
1
1
1
0
1
1
1
0
400
-200
200
0
0
1
ticks

@#$#@#$#@
WHAT IS IT?
-----------
This section could give a general understanding of what the model is trying to show or explain.


HOW IT WORKS
------------
This section could explain what rules the agents use to create the overall behavior of the model.


HOW TO USE IT
-------------
This section could explain how to use the model, including a description of each of the items in the interface tab.


THINGS TO NOTICE
----------------
This section could give some ideas of things for the user to notice while running the model.


THINGS TO TRY
-------------
This section could give some ideas of things for the user to try to do (move sliders, switches, etc.) with the model.


EXTENDING THE MODEL
-------------------
This section could give some ideas of things to add or change in the procedures tab to make the model more complicated, detailed, accurate, etc.


NETLOGO FEATURES
----------------
This section could point out any especially interesting or unusual features of NetLogo that the model makes use of, particularly in the Procedures tab.  It might also point out places where workarounds were needed because of missing features.


RELATED MODELS
--------------
This section could give the names of models in the NetLogo Models Library or elsewhere which are of related interest.


CREDITS AND REFERENCES
----------------------
This section could contain a reference to the model's URL on the web if it has one, as well as any other necessary credits or references.
@#$#@#$#@
default
true
0
Polygon -7500403 true true 150 5 40 250 150 205 260 250

airplane
true
0
Polygon -7500403 true true 150 0 135 15 120 60 120 105 15 165 15 195 120 180 135 240 105 270 120 285 150 270 180 285 210 270 165 240 180 180 285 195 285 165 180 105 180 60 165 15

arrow
true
0
Polygon -7500403 true true 150 0 0 150 105 150 105 293 195 293 195 150 300 150

box
false
0
Polygon -7500403 true true 150 285 285 225 285 75 150 135
Polygon -7500403 true true 150 135 15 75 150 15 285 75
Polygon -7500403 true true 15 75 15 225 150 285 150 135
Line -16777216 false 150 285 150 135
Line -16777216 false 150 135 15 75
Line -16777216 false 150 135 285 75

bug
true
0
Circle -7500403 true true 96 182 108
Circle -7500403 true true 110 127 80
Circle -7500403 true true 110 75 80
Line -7500403 true 150 100 80 30
Line -7500403 true 150 100 220 30

butterfly
true
0
Polygon -7500403 true true 150 165 209 199 225 225 225 255 195 270 165 255 150 240
Polygon -7500403 true true 150 165 89 198 75 225 75 255 105 270 135 255 150 240
Polygon -7500403 true true 139 148 100 105 55 90 25 90 10 105 10 135 25 180 40 195 85 194 139 163
Polygon -7500403 true true 162 150 200 105 245 90 275 90 290 105 290 135 275 180 260 195 215 195 162 165
Polygon -16777216 true false 150 255 135 225 120 150 135 120 150 105 165 120 180 150 165 225
Circle -16777216 true false 135 90 30
Line -16777216 false 150 105 195 60
Line -16777216 false 150 105 105 60

car
false
0
Polygon -7500403 true true 300 180 279 164 261 144 240 135 226 132 213 106 203 84 185 63 159 50 135 50 75 60 0 150 0 165 0 225 300 225 300 180
Circle -16777216 true false 180 180 90
Circle -16777216 true false 30 180 90
Polygon -16777216 true false 162 80 132 78 134 135 209 135 194 105 189 96 180 89
Circle -7500403 true true 47 195 58
Circle -7500403 true true 195 195 58

circle
false
0
Circle -7500403 true true 0 0 300

circle 2
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240

cow
false
0
Polygon -7500403 true true 200 193 197 249 179 249 177 196 166 187 140 189 93 191 78 179 72 211 49 209 48 181 37 149 25 120 25 89 45 72 103 84 179 75 198 76 252 64 272 81 293 103 285 121 255 121 242 118 224 167
Polygon -7500403 true true 73 210 86 251 62 249 48 208
Polygon -7500403 true true 25 114 16 195 9 204 23 213 25 200 39 123

cylinder
false
0
Circle -7500403 true true 0 0 300

dot
false
0
Circle -7500403 true true 90 90 120

face happy
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 255 90 239 62 213 47 191 67 179 90 203 109 218 150 225 192 218 210 203 227 181 251 194 236 217 212 240

face neutral
false
0
Circle -7500403 true true 8 7 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Rectangle -16777216 true false 60 195 240 225

face sad
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 168 90 184 62 210 47 232 67 244 90 220 109 205 150 198 192 205 210 220 227 242 251 229 236 206 212 183

fish
false
0
Polygon -1 true false 44 131 21 87 15 86 0 120 15 150 0 180 13 214 20 212 45 166
Polygon -1 true false 135 195 119 235 95 218 76 210 46 204 60 165
Polygon -1 true false 75 45 83 77 71 103 86 114 166 78 135 60
Polygon -7500403 true true 30 136 151 77 226 81 280 119 292 146 292 160 287 170 270 195 195 210 151 212 30 166
Circle -16777216 true false 215 106 30

flag
false
0
Rectangle -7500403 true true 60 15 75 300
Polygon -7500403 true true 90 150 270 90 90 30
Line -7500403 true 75 135 90 135
Line -7500403 true 75 45 90 45

flower
false
0
Polygon -10899396 true false 135 120 165 165 180 210 180 240 150 300 165 300 195 240 195 195 165 135
Circle -7500403 true true 85 132 38
Circle -7500403 true true 130 147 38
Circle -7500403 true true 192 85 38
Circle -7500403 true true 85 40 38
Circle -7500403 true true 177 40 38
Circle -7500403 true true 177 132 38
Circle -7500403 true true 70 85 38
Circle -7500403 true true 130 25 38
Circle -7500403 true true 96 51 108
Circle -16777216 true false 113 68 74
Polygon -10899396 true false 189 233 219 188 249 173 279 188 234 218
Polygon -10899396 true false 180 255 150 210 105 210 75 240 135 240

house
false
0
Rectangle -7500403 true true 45 120 255 285
Rectangle -16777216 true false 120 210 180 285
Polygon -7500403 true true 15 120 150 15 285 120
Line -16777216 false 30 120 270 120

leaf
false
0
Polygon -7500403 true true 150 210 135 195 120 210 60 210 30 195 60 180 60 165 15 135 30 120 15 105 40 104 45 90 60 90 90 105 105 120 120 120 105 60 120 60 135 30 150 15 165 30 180 60 195 60 180 120 195 120 210 105 240 90 255 90 263 104 285 105 270 120 285 135 240 165 240 180 270 195 240 210 180 210 165 195
Polygon -7500403 true true 135 195 135 240 120 255 105 255 105 285 135 285 165 240 165 195

line
true
0
Line -7500403 true 150 0 150 300

line half
true
0
Line -7500403 true 150 0 150 150

pentagon
false
0
Polygon -7500403 true true 150 15 15 120 60 285 240 285 285 120

person
false
0
Circle -7500403 true true 110 5 80
Polygon -7500403 true true 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105

plant
false
0
Rectangle -7500403 true true 135 90 165 300
Polygon -7500403 true true 135 255 90 210 45 195 75 255 135 285
Polygon -7500403 true true 165 255 210 210 255 195 225 255 165 285
Polygon -7500403 true true 135 180 90 135 45 120 75 180 135 210
Polygon -7500403 true true 165 180 165 210 225 180 255 120 210 135
Polygon -7500403 true true 135 105 90 60 45 45 75 105 135 135
Polygon -7500403 true true 165 105 165 135 225 105 255 45 210 60
Polygon -7500403 true true 135 90 120 45 150 15 180 45 165 90

sheep
false
0
Rectangle -7500403 true true 151 225 180 285
Rectangle -7500403 true true 47 225 75 285
Rectangle -7500403 true true 15 75 210 225
Circle -7500403 true true 135 75 150
Circle -16777216 true false 165 76 116

square
false
0
Rectangle -7500403 true true 30 30 270 270

square 2
false
0
Rectangle -7500403 true true 30 30 270 270
Rectangle -16777216 true false 60 60 240 240

star
false
0
Polygon -7500403 true true 151 1 185 108 298 108 207 175 242 282 151 216 59 282 94 175 3 108 116 108

target
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240
Circle -7500403 true true 60 60 180
Circle -16777216 true false 90 90 120
Circle -7500403 true true 120 120 60

tree
false
0
Circle -7500403 true true 118 3 94
Rectangle -6459832 true false 120 195 180 300
Circle -7500403 true true 65 21 108
Circle -7500403 true true 116 41 127
Circle -7500403 true true 45 90 120
Circle -7500403 true true 104 74 152

triangle
false
0
Polygon -7500403 true true 150 30 15 255 285 255

triangle 2
false
0
Polygon -7500403 true true 150 30 15 255 285 255
Polygon -16777216 true false 151 99 225 223 75 224

truck
false
0
Rectangle -7500403 true true 4 45 195 187
Polygon -7500403 true true 296 193 296 150 259 134 244 104 208 104 207 194
Rectangle -1 true false 195 60 195 105
Polygon -16777216 true false 238 112 252 141 219 141 218 112
Circle -16777216 true false 234 174 42
Rectangle -7500403 true true 181 185 214 194
Circle -16777216 true false 144 174 42
Circle -16777216 true false 24 174 42
Circle -7500403 false true 24 174 42
Circle -7500403 false true 144 174 42
Circle -7500403 false true 234 174 42

turtle
true
0
Polygon -10899396 true false 215 204 240 233 246 254 228 266 215 252 193 210
Polygon -10899396 true false 195 90 225 75 245 75 260 89 269 108 261 124 240 105 225 105 210 105
Polygon -10899396 true false 105 90 75 75 55 75 40 89 31 108 39 124 60 105 75 105 90 105
Polygon -10899396 true false 132 85 134 64 107 51 108 17 150 2 192 18 192 52 169 65 172 87
Polygon -10899396 true false 85 204 60 233 54 254 72 266 85 252 107 210
Polygon -7500403 true true 119 75 179 75 209 101 224 135 220 225 175 261 128 261 81 224 74 135 88 99

wheel
false
0
Circle -7500403 true true 3 3 294
Circle -16777216 true false 30 30 240
Line -7500403 true 150 285 150 15
Line -7500403 true 15 150 285 150
Circle -7500403 true true 120 120 60
Line -7500403 true 216 40 79 269
Line -7500403 true 40 84 269 221
Line -7500403 true 40 216 269 79
Line -7500403 true 84 40 221 269

x
false
0
Polygon -7500403 true true 270 75 225 30 30 225 75 270
Polygon -7500403 true true 30 75 75 30 270 225 225 270

@#$#@#$#@
NetLogo 4.1.3
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
default
0.0
-0.2 0 1.0 0.0
0.0 1 1.0 0.0
0.2 0 1.0 0.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180

@#$#@#$#@
0
@#$#@#$#@
