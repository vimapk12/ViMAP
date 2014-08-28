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


breed [wabbits wabbit]
breed [blocks block]
breed [sensors sensor]
breed [agent-kinds kind]
breed [args arg]

;;NEEDED FOR MEASURE LINKING
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
 tagentkind
 tpenwidth
 tpencolor
]
 
breed [ flags flag ]

wabbits-own 
[
	; if true, the pen was down before the wabbit wrapped around the screen. 
	; Only used for wrapping around (so you can
	; put the pen back down after the turtle wraps around, 
	; if and only if it was down before.
	pen-was-down 
                  
     ; "yellow-ball", "blue-ball", or "red-ball"
     agent-kind-string 
     
     ; counts the number of flags/markers that have been dropped so far in the run
     flag-counter 
     odometer            ;total distance covered
     distfromlast        ;dist since last measure point
     odistfromlast       ;last measure points distfromlast (for accel)
     
     secret-number   ;;SECRET NUMBER
     repeat-num

     ; how much faster than initial speed a wabbit is moving.
     ; can be negative, but starts at 0
     bonus-speed 
     initial-x
     initial-y
     previous-x
     previous-y
     
     has-food
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
  ; the name that will appear on the block
  block-name 
  arg-list
  is-observer
  is-basic
  is-set-update
  display-name
  return-type ; "int", "real", "boolean", or uninitialized to mean none
  label-after-arg ; label after first argument, for example, for infix blocks like "x + y", where "+" is a label
  category   ; the name of the block's category, from categories-list (if any)
]

sensors-own
[
  sensor-name
  sensor-type
]

patches-own
[
  is-food
  is-nest
  chemical-scent
]

globals
   [
    ; tells what the wabbit's x-coordinate would be 
    ; if it went forward the given distance
    future-x 
    
    number-of-steps
    color-for-draw
    
    ;;NEEDED FOR MEASURE LINKING
    measure-points
    
    wabbits-list
    wabbit-kind-list
    
    ; list of blocks used to populate the toolbars or
    ;  palettes in Java construction-world
    blocks-list 
    
    ; list of sensors for Java construction-world
    sensors-list 
    
    ; list of agent-kinds in the model (breeds)
    agent-kind-list 
    predicate-list
    comp-int-left-vars
    comp-vars-left-vars
    comp-vars-right-vars
    
    ; list of data types to report to Java for graphing after each cycle
    chart-data-name-list 
    
    ; list of category names for blocks
    categories-list
        
    measure-option-string-lists
    measure-option-command-list
    
    var-name-list
    
    called-set-name
    
    can-highlight-agents
    last-cycle
    
    NaN
   ]

;;;;;;;;;;;

; sets up the screen
to setup 
 
  clear-all
  set-defaults
  color-background-patches
  make-other-stuff
  create-blocks-list
  create-sensors-list
  create-agent-kind-list
  create-predicate-list
  create-comp-int-list
  create-comp-vars-lists
  create-chart-data-name-list
  create-categories-list
  ;;NEEDED FOR MEASURE LINKING
  set measure-points []
  create-measure-option-string-lists
  create-measure-option-command-list
  create-var-name-list
  
  reset-ticks    ;; creates ticks and initializes them to 0
end

to setup-cycle
end

to takedown-cycle

  diffuse chemical-scent 0.5
  ask patches 
  [ 
    set chemical-scent chemical-scent * 0.9
    recolor-patch 
  ]
end

to recolor-patch  ;; patch procedure
  ;; give color to nest and food sources
  if not is-nest and not is-food
  [ set pcolor scale-color green chemical-scent 0.1 5 ]
end

to create-var-name-list
  set var-name-list
  [
    "the turtle's heading" 
    "the turtle's odometer" 
    "the distance from last measure-point" 
    "the turtle's speed" 
    "change in distance covered"
    "pen width"
    "pen color"
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
  
  set tlist []
  set tlist lput "Pen Width" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists
  
  set tlist []
  set tlist lput "Pen Color" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists
  
  set tlist []
  set tlist lput "Heading" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists
end

to create-measure-option-command-list
  set measure-option-command-list []
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 5 graph" measure-option-command-list
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 6 graph" measure-option-command-list
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 8 graph" measure-option-command-list
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 9 graph" measure-option-command-list
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 10 graph" measure-option-command-list
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 4 graph" measure-option-command-list
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
  let result-string ""
  let cpoint nobody
  let cpoint2 nobody
  let crad nobody
  
  user-message "Click and drag to define center and radius..."
  
  while [not mouse-down?] [ ]
  set result-string (word (precision mouse-xcor 2)"," (precision mouse-ycor 2))
  
  create-turtles 1 
  [ 
  set shape "circle" 
  set color black 
  set size 5 
  setxy mouse-xcor mouse-ycor 
  set cpoint self 
  ]
  create-turtles 1 
  [ 
  	set shape "circle" 
  	set color red 
  	set size 1 
  	setxy mouse-xcor mouse-ycor 
  	set cpoint2 self 
  ]
  create-turtles 1 
  [ 
  set shape "line half" 
  set crad self 
  setxy mouse-xcor mouse-ycor 
  set color black
  ]
  
  display
  let radius 0
  let d 0
  while [mouse-down?]
  [
    ask cpoint [ set d distancexy mouse-xcor mouse-ycor ]
    ask crad 
    [ 
    	set size 2 * d  
    	if (mouse-xcor != xcor or mouse-ycor != ycor) 
    	[ set heading towardsxy mouse-xcor mouse-ycor] 
	]
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

to create-categories-list
  set categories-list
  [
    "Control"
    "Movement"
    "Pen"
    "Secret Number"   ;;;SECRET NUMBER
  ]
end

to create-chart-data-name-list
  set chart-data-name-list 
  [
    "distance-from-start" 
    "distance-from-previous"
  ]
end

to-report distance-from-start [ current-agent-kind ]
  let result 0
  
  ifelse not any? wabbits with [ agent-kind-string = current-agent-kind ]
  [set result " "]
  [
    ask wabbits with [ agent-kind-string = current-agent-kind ]
    [
      set result result + distancexy initial-x initial-y
      set result result / (count wabbits with [ agent-kind-string = current-agent-kind] )
      set result precision result 1
    ]
  ]
  
  report result
end

to-report distance-from-previous [current-agent-kind]
  let result 0
  
  ifelse not any? wabbits with [ agent-kind-string = current-agent-kind ]
  [set result " "]
  [
    ask wabbits with [ agent-kind-string = current-agent-kind ]
    [
      ifelse previous-x = "" or previous-y = ""
      [set result " "]
      [
        set result result + distancexy previous-x previous-y
        set result result / (count wabbits with [ agent-kind-string = current-agent-kind ] )
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
  tick
end

to create-predicate-list
  set predicate-list ["has-food" "at-nest" "food-here"]
end

to create-comp-int-list
  set comp-int-left-vars ["heading" "step-size"]
end

to create-comp-vars-lists
  set comp-vars-left-vars ["heading" "step-size"]
  set comp-vars-right-vars ["heading" "step-size"]
end

to-report java-food-here [aWho]
  let food-radius 2
  let result true
  ask turtle aWho
  [
  ;  set result [is-food] of patch-here
  set result any? patches in-radius food-radius with [is-food]
  ]
  report result
end

to-report java-at-nest [aWho]
  let result true
  ask turtle aWho
  [
    set result [is-nest] of patch-here
  ]
  report result
end

to-report java-has-food [aWho]
  let result true
  ask turtle aWho
  [
    set result has-food
  ]
  report result
end

to-report java-heading-upward [aWho]
  let result false
  ask turtle aWho
  [
    if (heading >= 0 and heading < 90) or (heading > 270 and heading <= 360)
    [set result true]
  ]
  report result
end

to-report java-is-using-repeat
  report true
end

to-report java-is-image-computation
  report false
end

to reset
setup
end

to create-sensors-list
  set sensors-list []

  ;;; END OF SENSOR DEFINITIONS ;;;

end

to create-blocks-list
  set blocks-list []
  
  ;;; PUT BLOCK DEFINITIONS HERE: ;;;
  create-blocks 1
  [
    set block-name "set-step-size"
    set is-observer false
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 0
      set max-value 400
      set min-value 0
    ]
    set arg-list lput max-one-of args [who] arg-list
    set category "Movement"
    set is-basic true
    ; other variables not applicable
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  
  create-blocks 1
  [
    set block-name "place-chemical"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
  create-blocks 1
  [
    set block-name "face-chemical"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
  
    create-blocks 1
  [
    set block-name "i-have-food"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
    create-blocks 1
  [
    set block-name "face-nest"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
   create-blocks 1
  [
    set block-name "i-don't-have-food"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
  create-blocks 1
  [
    set block-name "remove-food-here"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
  
  create-blocks 1
  [
    set block-name "go-forward"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
    
    ;;NEEDED FOR MEASURE LINKING
      create-blocks 1
  [
    set block-name "place-measure-point"
    ;set category "Pen"
    set arg-list []
    set is-observer false
    set is-basic false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
            create-blocks 1
  [
    set block-name "clear-measure-points"
    ;set category "Pen"
    set arg-list []
    set is-observer false
    set is-basic false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
                  create-blocks 1
  [
    set block-name "start-measuring"
    ;set category "Pen"
    set arg-list []
    set is-observer false
    set is-basic false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
              create-blocks 1
  [
    set block-name "random-turn"
    set category "Movement"
    
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 90
      set max-value 180
      set min-value 0
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    set is-basic true
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
      set max-value 360
      set min-value 0
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    set is-basic true
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
      set max-value 360
      set min-value 0
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
  create-blocks 1
  [
    set block-name "pen-up"
    set category "Pen"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list 
      
        create-blocks 1
  [
    set block-name "plant-flag"
    set category "Pen"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
      
      
  ;;; END OF BLOCK DEFINITIONS ;;;
end

to create-agent-kind-list
  set agent-kind-list [] 
  
  create-agent-kinds 1
  [
    set name "ant"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []
    set primitives-list lput "place-measure-point" primitives-list
    set primitives-list lput "clear-measure-points" primitives-list
    set primitives-list lput "start-measuring" primitives-list

    set primitives-list lput "set-step-size" primitives-list
    set primitives-list lput "go-forward" primitives-list
  
    set primitives-list lput "right" primitives-list
    set primitives-list lput "left" primitives-list
    set primitives-list lput "random-turn" primitives-list
    set primitives-list lput "face-nest" primitives-list
    set primitives-list lput "face-chemical" primitives-list
    
    set primitives-list lput "i-have-food" primitives-list
    set primitives-list lput "i-don't-have-food" primitives-list
    set primitives-list lput "remove-food-here" primitives-list
    set primitives-list lput "place-chemical" primitives-list
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
end

to java-place-chemical
  set chemical-scent chemical-scent + 60
end

to java-i-have-food
  set has-food true
end

to java-i-don't-have-food
  set has-food false
end

to java-remove-food-here
  let food-radius 2
  if any? patches in-radius food-radius with [is-food]
  [
    ask one-of patches in-radius food-radius with [is-food]
    [
      set is-food false
      set pcolor black
    ]
  ] 
;  ask patch-here
;  [
;    if is-food
;    [
;      set is-food false
;      set pcolor black
;    ]
;  ]
end

to create-wabbits-list
  set wabbits-list sort [who] of wabbits
end

to create-wabbit-kind-list
  create-wabbits-list
  set wabbit-kind-list []
  
  foreach wabbits-list
  [
    set wabbit-kind-list lput [agent-kind-string] of turtle ? wabbit-kind-list
  ]
end

to java-set-step-size [ aspeed ]
  set bonus-speed aspeed
  if (aspeed < 0) [ set bonus-speed 0 ]
end

to-report java-heading [aWho]
  let result 0
  ask turtle aWho
  [set result heading]
  report result
end

to-report java-step-size [aWho]
  let result 0
  ask turtle aWho
  [set result bonus-speed]
  report result
end



to java-go-forward 
  let forward-distance bonus-speed
  if forward-distance < 0
  [set forward-distance 0]
  
  let moved 0
  while [moved < forward-distance and can-move? 1]
  [
   jump 1 
   set moved moved + 1
  ]
  ;;turtle variables that will be harvested at meaure points.
  set odometer odometer + moved
  if any? measurepoints
  [
    if distfromlast = NaN
    [set distfromlast 0]
    set distfromlast distfromlast + moved
  ]
end

to java-right [amount-number]
  right amount-number
end

to java-left [amount-number]
  left amount-number
end

to java-random-turn [amount-number]
  let rand (random-float amount-number * 2)
  right rand - amount-number
end

to java-face-chemical
  if (chemical-scent >= 0.05) and (chemical-scent < 2)
[
  let scent-ahead chemical-scent-at-angle 0
  let scent-right chemical-scent-at-angle 45
  let scent-left chemical-scent-at-angle -45
  if (scent-right > scent-ahead) or (scent-left > scent-ahead)
  [ 
    ifelse scent-right > scent-left
    [rt 45]
    [lt 45] 
  ]
]
end

to-report chemical-scent-at-angle [angle]
  let p patch-right-and-ahead angle 1
  if p = nobody [ report 0 ]
  report [chemical-scent] of p
end

to java-face-nest
  facexy 0 0
end

to java-start-measuring
      ask wabbits
    [
      set distfromlast NaN        ;dist since last measure point
      set odistfromlast NaN      ;last measure points distfromlast (for accel)
      set odometer 0
    ]
end

to java-clear-measure-points
    ask measurepoints [die]
    ask wabbits
    [
      set distfromlast NaN        ;dist since last measure point
      set odistfromlast NaN      ;last measure points distfromlast (for accel)
      set odometer 0
    ]
end

;;NEEDED FOR MEASURE LINKING
to java-place-measure-point
  hatch-measurepoints 1
  [
   set measure-points lput self measure-points
   set size 15
   set shape "flag"
   set color black 
   
   set tagentkind [agent-kind-string] of myself
   set tcycles count measurepoints - 1
   set theading [heading] of myself
   set todometer [ odometer ] of myself
   ifelse [distfromlast] of myself = NaN or [odistfromlast] of myself = NaN 
   [set taccel NaN]
   [set taccel [ distfromlast - odistfromlast ] of myself]
   set tdistfromlast [ distfromlast ] of myself
   set tspeed [bonus-speed] of myself
   set tcolor [ color ] of myself
   set tpenwidth [ pen-size ] of myself
   set tpencolor [ color ] of myself
   
   set label-color black
   set label tcycles
  ]
  set odistfromlast distfromlast
  set distfromlast 0
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

; runresult converts the String variable into its 
; NetLogo code equivalent (a property name)
  report [runresult property] of item index list-name 
end

to set-defaults
  set-default-shape wabbits "circle"
  set NaN -9007199254740992
  set number-of-steps 0
  set can-highlight-agents false
  set last-cycle false
end

to color-background-patches
  ask patches
       [set pcolor black]
  create-nest
end

to create-nest
  ask patches
  [
    set is-nest false
    set is-food false
  ]
  ask patches with [distancexy 0 0 < 4]
  [
    set is-nest true
    set pcolor blue + 2
  ]
  
  ask patches with [distancexy -15 15 < 4]
  [
    set is-food true
    set pcolor orange + 2
  ]
  
    ask patches with [distancexy 15 -15 < 4]
  [
    set is-food true
    set pcolor orange + 2
  ]
end

to make-other-stuff
  create-wabbits 20
        [setxy random-xcor random-ycor
        set heading random 360
        set color red
        set shape "ant"
        set agent-kind-string "ant"
        set distfromlast NaN
        set odistfromlast NaN
       ]
  ask wabbits
       [set size 5
        set pen-size 5
        
        set has-food false
        ; pen is up now
        set pen-was-down false 
        set bonus-speed 0
        set flag-counter 0
        
        set secret-number random 101    ;SECRET NUMBER
        set repeat-num random 5 + 2     ;REPEAT NUMBER
        
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
    
      let datarep (list who tcolor (word "\"" tagentkind "\"") tcycles theading todometer tdistfromlast tspeed taccel tpenwidth tpencolor) 
      set result lput datarep result 
    ]
  ]
  report result
end

to-report get-measures-for [an-agent-kind]
  let result []
  let relevant-measures measurepoints with [ tagentkind = an-agent-kind ]
  let relevant-list sort relevant-measures
  foreach relevant-list 
  [
    ask ? 
    [ 
      if (is-string? tdistfromlast) 
      [ set tdistfromlast 0 ]
      
      let datarep (list who tcolor (word "\"" tagentkind "\"") tcycles theading todometer tdistfromlast tspeed taccel tpenwidth tpencolor) 
      set result lput datarep result 
    ]
  ]
  report result
end

to-report get-measures-for-filtered [an-agent-kind a-measurepoint-creator]
  report get-measures-for an-agent-kind
end

to-report get-agent-kinds-as-csv
  let retn ""
  foreach agent-kind-list
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
447
468
30
30
7.0
1
10
1
1
1
0
1
1
1
-30
30
-30
30
0
0
1
ticks
30.0

@#$#@#$#@
## ## WHAT IS IT?

This section could give a general understanding of what the model is trying to show or explain.

## ## HOW IT WORKS

This section could explain what rules the agents use to create the overall behavior of the model.

## ## HOW TO USE IT

This section could explain how to use the model, including a description of each of the items in the interface tab.

## ## THINGS TO NOTICE

This section could give some ideas of things for the user to notice while running the model.

## ## THINGS TO TRY

This section could give some ideas of things for the user to try to do (move sliders, switches, etc.) with the model.

## ## EXTENDING THE MODEL

This section could give some ideas of things to add or change in the procedures tab to make the model more complicated, detailed, accurate, etc.

## ## NETLOGO FEATURES

This section could point out any especially interesting or unusual features of NetLogo that the model makes use of, particularly in the Procedures tab.  It might also point out places where workarounds were needed because of missing features.

## ## RELATED MODELS

This section could give the names of models in the NetLogo Models Library or elsewhere which are of related interest.

## ## CREDITS AND REFERENCES

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

ant
true
0
Polygon -7500403 true true 136 61 129 46 144 30 119 45 124 60 114 82 97 37 132 10 93 36 111 84 127 105 172 105 189 84 208 35 171 11 202 35 204 37 186 82 177 60 180 44 159 32 170 44 165 60
Polygon -7500403 true true 150 95 135 103 139 117 125 149 137 180 135 196 150 204 166 195 161 180 174 150 158 116 164 102
Polygon -7500403 true true 149 186 128 197 114 232 134 270 149 282 166 270 185 232 171 195 149 186
Polygon -16777216 true false 225 66 230 107 159 122 161 127 234 111 236 106
Polygon -16777216 true false 78 58 99 116 139 123 137 128 95 119
Polygon -16777216 true false 48 103 90 147 129 147 130 151 86 151
Polygon -16777216 true false 65 224 92 171 134 160 135 164 95 175
Polygon -16777216 true false 235 222 210 170 163 162 161 166 208 174
Polygon -16777216 true false 249 107 211 147 168 147 168 150 213 150

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
Polygon -16777216 true false 256 83 266 89 258 90
Polygon -16777216 true false 97 84 137 81 149 95 153 116 148 122 138 127 125 127 121 121 112 112 103 105 103 96 107 89
Polygon -16777216 true false 194 122 175 123 171 129 168 137 176 155 187 157 202 164 205 156 209 146 204 136
Polygon -16777216 true false 71 141 70 154 77 157 95 161 97 147 97 134 78 125 67 125
Polygon -16777216 true false 60 237 80 239 83 249 64 249
Polygon -16777216 true false 181 238 195 239 198 249 179 249

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
Polygon -16777216 true false 44 131 21 87 15 86 0 120 15 150 0 180 13 214 20 212 45 166
Polygon -16777216 true false 135 195 119 235 95 218 76 210 46 204 60 165
Polygon -16777216 true false 75 45 83 77 71 103 86 114 166 78 135 60
Polygon -7500403 true true 30 136 151 77 226 81 280 119 292 146 292 160 287 170 270 195 195 210 151 212 30 166
Circle -16777216 true false 215 106 30

flag
false
1
Rectangle -7500403 true false 60 15 75 300
Polygon -7500403 true false 90 150 270 90 90 30
Line -7500403 false 75 135 90 135
Line -7500403 false 75 45 90 45
Polygon -1184463 true false 90 15 300 90 90 165 90 150 270 90 90 30

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
Polygon -16777216 true false 153 80 87 158 114 102 125 193 101 269 135 269 150 219 162 266 202 265 178 192 186 103 221 162
Polygon -16777216 true false 111 97 189 96 225 141 217 161 187 120 115 121 86 156 74 138

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

squirrel
false
0
Polygon -7500403 true true 87 267 106 290 145 292 157 288 175 292 209 292 207 281 190 276 174 277 156 271 154 261 157 245 151 230 156 221 171 209 214 165 231 171 239 171 263 154 281 137 294 136 297 126 295 119 279 117 241 145 242 128 262 132 282 124 288 108 269 88 247 73 226 72 213 76 208 88 190 112 151 107 119 117 84 139 61 175 57 210 65 231 79 253 65 243 46 187 49 157 82 109 115 93 146 83 202 49 231 13 181 12 142 6 95 30 50 39 12 96 0 162 23 250 68 275
Polygon -16777216 true false 237 85 249 84 255 92 246 95
Line -16777216 false 221 82 213 93
Line -16777216 false 253 119 266 124
Line -16777216 false 278 110 278 116
Line -16777216 false 149 229 135 211
Line -16777216 false 134 211 115 207
Line -16777216 false 117 207 106 211
Line -16777216 false 91 268 131 290
Line -16777216 false 220 82 213 79
Line -16777216 false 286 126 294 128
Line -16777216 false 193 284 206 285
Polygon -7500403 false true 15 135 15 180
Polygon -16777216 false false 30 241 8 153 21 104 57 53 153 10 66 57 29 104 17 149 35 239 38 237 41 237 25 148 43 100 77 61 170 12 91 65 53 104 35 151 50 232

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
NetLogo 5.1.0
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
default
0.0
-0.2 1 1.0 0.0
0.0 1 1.0 0.0
0.2 1 1.0 0.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180

@#$#@#$#@
0
@#$#@#$#@
