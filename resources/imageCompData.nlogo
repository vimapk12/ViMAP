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
breed [followers follower]
breed [blocks block]
breed [set-types set-type]
breed [sensors sensor]
breed [agent-kinds agent-kind]
breed [args arg]
breed [my-agent-sets my-agent-set]
breed [set-datas set-data]

;;NEEDED FOR MEASURE LINKING
breed [ measurepoints measurepoint ]
measurepoints-own [
 tticks
 tcolor
 tcycles
 todometer
 tdistfromlast
 tspeed
 taccel
 tagentkind
 tpenwidth
 measurepoint-creator
]

wabbits-own [pen-was-down ;; if true, the pen was down before the wabbit wrapped around the screen. Only used for wrapping around (so you can
                  ;; put the pen back down after the turtle wraps around, if and only if it was down before.
             agent-kind-string ;; "yellow-ball", "blue-ball", or "red-ball"
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
set-datas-own
[
  set-name
  
  odometer            ;total distance covered
  distfromlast        ;dist since last measure point
  odistfromlast       ;last measure points distfromlast (for accel)
  set-bonus-speed
  set-pen-width
]
followers-own
[
  old-color              
  old-size
]
blocks-own  
[
  block-name ; the name that will appear on the block
  arg-list
  is-observer
  is-basic
  is-set-update
  
  display-name
  return-type
  label-after-arg
  
  ; the name of the block's category, from categories-list (if any)
  category
]
set-types-own
[
  set-type-name
  arg-list
]
sensors-own
[
  sensor-name
  sensor-type
]
my-agent-sets-own
[
  name
  agents
]
turtles-own
[
  image
  bonus-speed
]

globals
   [
    future-x ;; tells what the wabbit's x-coordinate would be if it went forward the given distance
    
    number-of-steps
    color-for-draw
    
    first-flag
    second-flag
    flag-distance
    
    wabbits-list
    wabbit-kind-list
    blocks-list ;; list of blocks used to populate the toolbars / palettes in Java construction-world
    set-types-list ;; list of set types the user can create
    sensors-list ;; list of sensors for Java construction-world
    agent-kind-list ;; list of agent-kind in the model (breeds)
    predicate-list
    comp-int-left-vars
    comp-vars-left-vars
    comp-vars-right-vars
    set-predicate-list
    chart-data-name-list ;; list of data types to report to Java for graphing after each cycle
    my-agent-sets-list
    
    ; list of category names for blocks
    categories-list
        
    measure-option-string-lists
    measure-option-command-list
    
    var-name-list
    
    can-highlight-agents
    last-cycle
    
    other-agents
    
    called-set-name ;; maybe be 0 or "" (empty string) if no called set.
    
    measure-points
    
    image-to-import
    
    NaN
   ]

;;;;;;;;;;;;;;;;;;;;;

to setup ;; sets up the screen
  let image-name image-to-import
  clear-all
  set image-to-import image-name
  
  color-background-patches
  set-defaults
  make-other-stuff
  create-my-agent-sets-list
  create-blocks-list
  create-set-types-list
  create-sensors-list
  create-agent-kind-list
  create-predicate-list
  create-comp-int-list
  create-comp-vars-lists
  create-chart-data-name-list
  create-categories-list
  create-measure-option-string-lists
  create-measure-option-command-list
  create-var-name-list
  hide-invisibles
  
  reset-ticks    ;; creates ticks and initializes them to 0
end

to hide-invisibles
  ask blocks [ht]
  ask set-types [ht]
  ask sensors [ht]
  ask agent-kinds [ht]
  ask args [ht]
  ask my-agent-sets [ht]
end

to create-var-name-list
  set var-name-list
  [
    "the turtle's odometer" 
    "the distance from last measure-point" 
    "the turtle's speed" 
    "change in distance covered"
    "pen width"
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
end

to create-measure-option-command-list
  set measure-option-command-list []
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 4 graph" measure-option-command-list
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 5 graph" measure-option-command-list
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 7 graph" measure-option-command-list
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 8 graph" measure-option-command-list
end

to-report get-list-as-csv [list-name]
  let result ""
  if length list-name = 0
  [report result]
  
  foreach list-name
  [
   set result (word result ? "," )
  ]
  report butlast result
end

to create-categories-list
  set categories-list
  [
    "Control"
    "Movement"
    "Pen"
    "Measure"
  ]
end

to create-my-agent-sets-list
  set my-agent-sets-list []
end

to java-clear-measure-points
  ask measurepoints [die]
  ask set-datas
  [
    set odometer 0            ;total distance covered
    set distfromlast NaN        ;dist since last measure point
    set odistfromlast NaN      ;last measure points distfromlast (for accel)
  ]
end

to java-start-measuring
      ask set-datas
    [
      set distfromlast NaN        ;dist since last measure point
      set odistfromlast NaN      ;last measure points distfromlast (for accel)
      set odometer 0
    ]
end

to create-set-data [a-set-name]
  if not any? set-datas with [set-name = a-set-name]
  [
    create-set-datas 1
    [
      set set-name a-set-name
      reset-set-data
      ht
    ]
    
    java-clear-measure-points
  ]
end

to reset-set-data
  set odometer 0            ;total distance covered
  set distfromlast NaN        ;dist since last measure point
  set odistfromlast NaN     ;last measure points distfromlast (for accel)
  set set-bonus-speed 0 
  set set-pen-width 1
end

to delete-set-data [a-set-name]
  ask set-datas with [set-name = a-set-name]
  [die]

  java-clear-measure-points
end

to-report center-radius-and-name

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
  set chart-data-name-list ["population"]
end

to-report population [current-agent-kind]
  report count turtles with [image = 1]
end

to create-predicate-list
  set predicate-list ["true" "false"]
   set set-predicate-list [
  "true"
  "false"
  "coin-flip"
 ]
end

; similar to create-predicate-list
to create-comp-int-list
  set comp-int-left-vars ["heading" "color"]
end

; similar to create-predicate-list
to create-comp-vars-lists
  set comp-vars-left-vars ["heading" "color"]
  set comp-vars-right-vars ["heading" "color"]
end

to-report java-color [aWho]
  let result 0
  ask turtle aWho
  [ set result color ]
  report result
end


to-report java-heading [aWho]
  let result 0
  ask turtle aWho
  [ set result heading ]
  report result
end
  
to-report coin-flip
  report random-float 1 < 0.5
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
  report true
end

to reset
setup
end

to create-sensors-list
  set sensors-list []

  ;;;;; END OF SENSOR DEFINITIONS ;;;;;

end

to create-set-types-list
  set set-types-list []
end

to create-blocks-list
  set blocks-list []
  
  ;;;;; PUT BLOCK DEFINITIONS HERE: ;;;;;
  
  create-blocks 1
  [
    set block-name "go-forward"
    set category "Movement"
    
    set arg-list []
    set is-observer false
    set is-basic true
    set is-set-update true
  ]
    set blocks-list lput max-one-of blocks [who] blocks-list
    
      create-blocks 1
  [
    set block-name "set-group-color"
    set category "Movement"
    
    set arg-list []
    set is-observer false
    set is-set-update true
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
    set is-basic true
    set is-set-update true
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
    set is-basic true
    set is-set-update true
    ; other variables not applicable
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
      set default-value 90
      set max-value 360
      set min-value 0
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    set is-basic false
    set is-set-update true
    ; other variables not applicable
  ]
    set blocks-list lput max-one-of blocks [who] blocks-list
    
    create-blocks 1 ;;;;AMY
  [
    set block-name "set-random-heading"
    set category "Movement"
    
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 0
      set max-value 360
      set min-value -360
    ]
    set arg-list lput max-one-of args [who] arg-list
    
    hatch-args 1
    [
      set arg-type "int"
      set default-value 360
      set max-value 360
      set min-value -360
    ]
    set arg-list lput max-one-of args [who] arg-list
    
    set label-after-arg " to "
    set is-observer false
    set is-basic false
    set is-set-update true
    ; other variables not applicable
  ]
    set blocks-list lput max-one-of blocks [who] blocks-list
    
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
    set block-name "step-size-plus"
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
    set is-basic false
    set is-set-update true
    ; other variables not applicable
  ]
    set blocks-list lput max-one-of blocks [who] blocks-list
    
    create-blocks 1
  [
    set block-name "step-size-minus"
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
    set is-basic false
    set is-set-update true
    ; other variables not applicable
  ]
    set blocks-list lput max-one-of blocks [who] blocks-list
    
;    create-blocks 1
;  [
;    set block-name "set-xy"
;    set category "Movement"
;    set arg-list []
;        hatch-args 1
;    [
;      set arg-type "int"
;      set default-value 0
;      set max-value max-pxcor
;      set min-value min-pxcor
;    ]
;    set arg-list lput max-one-of args [who] arg-list
;        hatch-args 1
;    [
;      set arg-type "int"
;      set default-value 0
;      set max-value max-pycor
;      set min-value min-pycor
;    ]
;    set arg-list lput max-one-of args [who] arg-list
;    set is-observer false
;    set is-basic false
;    set is-set-update false
;    ; other variables not applicable
;  ]
;      set blocks-list lput max-one-of blocks [who] blocks-list
    
    create-blocks 1
  [
    set block-name "reset-forward-values"
    set category "Movement"
   
    set arg-list []
    set is-observer false
    set is-basic false
    set is-set-update true
    ; other variables not applicable
  ]
    set blocks-list lput max-one-of blocks [who] blocks-list
    
  create-blocks 1
  [
    set block-name "pen-up"
    set category "Pen"
    set arg-list []
    set is-observer false
    set is-basic false
    set is-set-update false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
      create-blocks 1
  [
    set block-name "pen-down"
    set category "Pen"
    set arg-list []
    set is-observer false
    set is-basic true
    set is-set-update false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
      create-blocks 1
  [
    set block-name "stamp"
    set category "Pen"
    set arg-list []
    set is-observer false
    set is-basic false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
      create-blocks 1
  [
    set block-name "set-shape"
    set arg-list []
    hatch-args 1
    [
      set arg-type "enum"
      set enum-list ["circle" "square" "arrow"]
    ]
    set arg-list lput max-one-of args [who] arg-list
    set category "Pen"
    set is-observer false
    set is-basic false
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  create-blocks 1
  [
    set block-name "go-invisible"
    set category "Pen"
    set arg-list []
    set is-observer false
    set is-basic false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      create-blocks 1
  [
    set block-name "go-visible"
    set category "Pen"
    set arg-list []
    set is-observer false
    set is-basic false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      create-blocks 1
  [
    set block-name "my-place-measure-point"
    set display-name "place measure point"
    set category "Measure"
    set arg-list []
    set is-observer true
    set is-basic false
    set is-set-update false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
      create-blocks 1
  [
    set block-name "reset-measures"
    set display-name "clear measure points"
    set category "Measure"
    set arg-list []
    set is-observer true
    set is-basic false
    set is-set-update false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
                  create-blocks 1
  [
    set block-name "start-measuring"
    set category "Measure"
    set arg-list []
    set is-observer false
    set is-basic false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
      create-blocks 1
  [
    set block-name "face"
    set category "Movement"
      set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 0
      set max-value 200
      set min-value -200
    ; other variables not applicable
  ]
       set arg-list lput max-one-of args [who] arg-list
       
    hatch-args 1
    [
      set arg-type "int"
      set default-value 0
      set max-value 200
      set min-value -200
    ; other variables not applicable
  ]
       set arg-list lput max-one-of args [who] arg-list
       set is-observer false
       set is-basic false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
      create-blocks 1
  [
    set block-name "set"
    set arg-list []
    hatch-args 1
    [
      set arg-type "enum"
      set enum-list ["agent-size" "pen-width" "color" "step-size" "heading" ]
    ]
    set arg-list lput max-one-of args [who] arg-list
    hatch-args 1
    [
      set arg-type "enum"
      set enum-list ["equal to" "plus" "minus" "times" "divided by" "random up to"]
    ]
    set arg-list lput max-one-of args [who] arg-list
    hatch-args 1
    [
       set arg-type "int"
       set default-value 1
       set max-value 360
       set min-value 1
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    set is-basic false
    set category "Pen"
    set is-set-update true
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
 
  ;;;;; END OF BLOCK DEFINITIONS ;;;;;
end

to-report get-agent-kinds-as-csv
  let retn ""
  let my-set-datas sort set-datas
  if empty? my-set-datas
  [
    create-set-data "all"
    create-set-data "other"
    set my-set-datas sort set-datas
  ]
  
 ;  [report retn]
  
  foreach my-set-datas
  [
   set retn (word retn ([set-name] of ?) "," )
  ]
  report butlast retn
end

to create-agent-kind-list
  set agent-kind-list [] 
  
  create-agent-kinds 1
  [
    set name "controller"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []
    set primitives-list lput "set-step-size" primitives-list
    set primitives-list lput "go-forward" primitives-list
    set primitives-list lput "right" primitives-list
    set primitives-list lput "left" primitives-list
    set primitives-list lput "pen-up" primitives-list 
    set primitives-list lput "pen-down" primitives-list
    set primitives-list lput "stamp" primitives-list
    set primitives-list lput "set" primitives-list
    set primitives-list lput "go-invisible" primitives-list
    set primitives-list lput "go-visible" primitives-list
    set primitives-list lput "set-heading" primitives-list
    set primitives-list lput "set-group-color" primitives-list
    set primitives-list lput "reset-forward-values" primitives-list ; set level
    set primitives-list lput "face" primitives-list
    set primitives-list lput "set-random-heading" primitives-list
    set primitives-list lput "step-size-plus" primitives-list ; set level
    set primitives-list lput "step-size-minus" primitives-list ; set level
    set primitives-list lput "set-shape" primitives-list
    set primitives-list lput "my-place-measure-point" primitives-list
    set primitives-list lput "reset-measures" primitives-list
    set primitives-list lput "start-measuring" primitives-list
;    set primitives-list lput "set-xy" primitives-list
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
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

to java-reset-measures
  java-clear-measure-points
end

to-report java-is-repeat-var
  report true
end

to-report list-contains-name [ a-set-name ]
  if empty? my-agent-sets-list
  [report false]
  
  foreach my-agent-sets-list
  [
    if [name] of ? = a-set-name
    [report true]
  ]
  
  report false
end

to java-go-forward
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    let my-bonus-speed [set-bonus-speed] of called-set
    
    let current-agent-set agent-set called-set-name
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [ask followers [forward my-bonus-speed]]
      if called-set-name = "other"
      [ask other-agents [forward my-bonus-speed]]
    ]
    [
      ask [agents] of current-agent-set
      [forward my-bonus-speed]
    ]
    
    ask called-set
    [
      if any? measurepoints
      [
        if distfromlast = NaN
        [set distfromlast 0]
        set distfromlast distfromlast + set-bonus-speed
      ]
      set odometer odometer + set-bonus-speed
    ]
  ]
end

to java-left [amount-number]
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    let current-agent-set agent-set called-set-name
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [
        ask followers 
        [left amount-number]
      ]
      if called-set-name = "other"
      [
        ask other-agents 
        [left amount-number]
      ]
    ]
    [
      ask [agents] of current-agent-set
      [left amount-number]
    ]
  ]
end

to java-face [xcoord ycoord]
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [ 
    let current-agent-set agent-set called-set-name
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [
        ask followers 
        [facexy xcoord ycoord]
      ]
      if called-set-name = "other"
      [
        ask other-agents 
        [facexy xcoord ycoord]
      ]
    ]
    [
      ask [agents] of current-agent-set
      [facexy xcoord ycoord]
    ]
  ]
end

to java-set [ base-attrib operator-name opvalue ] 
  ; "agent-size" "pen-width" "color" "step-size" "heading"
  ; "equal to" "plus" "minus" "times" "divided by" "random up to"
  
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    if base-attrib = "agent-size" [ask called-set [java-set-size operator-name opvalue]]
    if base-attrib = "heading" [ask called-set [java-set-heading-2 operator-name opvalue]]
    
    let value 0
    ;if base-attrib = "agent-size" [ask called-set [set value set-size]]
    if base-attrib = "pen-width" [ask called-set [set value set-pen-width]]
    if base-attrib = "step-size" [ask called-set [set value set-bonus-speed]]
    ;if base-attrib = "heading" [ask called-set [set value set-heading]]
    
    if operator-name = "equal to" [set value opvalue]
    if operator-name = "plus" [set value value + opvalue]
    if operator-name = "minus" [set value value - opvalue]
    if operator-name = "times" [set value value * opvalue]
    if operator-name = "divided by"
    [
      if opvalue != 0
      [set value value / opvalue]
    ]
    if operator-name = "random up to" [set value random opvalue]
    
    ;if base-attrib = "agent-size" [java-set-size value]
    if base-attrib = "pen-width" [java-set-pen-width value]
    if base-attrib = "color" [java-set-color operator-name opvalue]
    if base-attrib = "step-size" [java-set-step-size value]
    ;if base-attrib = "heading" [java-set-heading value]
  ]
end

to java-set-pen-width [amount]
  if amount < 0
  [set amount 0]

  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    ask called-set
    [set set-pen-width amount]
    
    let current-agent-set agent-set called-set-name
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [
        ask followers 
        [set pen-size amount]
      ]
      if called-set-name = "other"
      [
        ask other-agents 
        [set pen-size amount]
      ]
    ]
    [
      ask [agents] of current-agent-set
      [set pen-size amount]
    ]
  ]
end

to set-size [opname amount]
  if opname = "equal to" 
  [
    ifelse amount > 0
    [set size amount]
    [set size 0]
  ]
    if opname = "plus" 
    [
      ifelse size + amount > 0
      [set size size + amount]
      [set size 0]
    ]
    if opname = "minus" 
    [
      ifelse size > amount
      [set size size - amount]
      [set size 0]
    ]
    if opname = "times" 
    [
      ifelse amount > 0
      [set size size * amount]
      [set size 0]
    ]
    if opname = "divided by"
    [
      if amount != 0
      [
        ifelse amount > 0
        [set size size / amount]
        [set size 0]
      ]
    ]
    if opname = "random up to" 
    [
      ifelse amount > 0
      [set size random amount]
      [set size 0]
    ]
end

to java-set-heading [angle]
  if angle >= 360 or angle < 0
  [set angle angle mod 360]
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    let current-agent-set agent-set called-set-name
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [
        ask followers 
        [set heading angle]
      ]
      if called-set-name = "other"
      [
        ask other-agents 
        [set heading angle]
      ]
    ]
    [
      ask [agents] of current-agent-set
      [set heading angle]
    ]
  ]
end

to java-set-heading-2 [opname angle]
  if angle >= 360 or angle < 0
  [set angle angle mod 360]
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    let current-agent-set agent-set called-set-name
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [
        ask followers 
        [set-heading opname angle]
      ]
      if called-set-name = "other"
      [
        ask other-agents 
        [set-heading opname angle]
      ]
    ]
    [
      ask [agents] of current-agent-set
      [set-heading opname angle]
    ]
  ]
end

to set-heading [opname angle]
  if opname = "equal to" 
  [set heading angle]
  if opname = "plus" 
  [right angle]
  if opname = "minus" 
  [left angle]
  if opname = "times" 
  [set heading (heading * angle) mod 360]
  if opname = "divided by"
  [
    if angle != 0
    [set heading heading / angle]
  ]
  if opname = "random up to" 
  [set heading random angle]
end

to java-set-size [opname amount]
  if amount < 0
  [set amount 0]

  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [ 
    let current-agent-set agent-set called-set-name
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [
        ask followers 
        [set-size opname amount]
      ]
      if called-set-name = "other"
      [
        ask other-agents 
        [set-size opname amount]
      ]
    ]
    [
      ask [agents] of current-agent-set
      [set-size opname amount]
    ]
  ]
end

to java-right [amount-number]
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    let current-agent-set agent-set called-set-name
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [
        ask followers 
        [right amount-number]
      ]
      if called-set-name = "other"
      [
        ask other-agents 
        [right amount-number]
      ]
    ]
    [
      ask [agents] of current-agent-set
      [right amount-number]
    ]
  ]
end

to-report random-heading-range [angle1 angle2]
  let fin_angle 0   ;; resulting angle
  ifelse (angle2 - angle1) >= 360 or (angle2 - angle1) <= -360 ;; if user enters angle greater than 360
  [ set fin_angle random 360 ] ;; can set to any angle.
  [
    let ang1 (angle1 mod 360) ;; convert all angles to (0, 359) form to reduce confusion
    let ang2 (angle2 mod 360)
    
    ifelse ang2 = ang1
    [ set fin_angle ang1]
    [
      ifelse ang2 > ang1
      [
        let temp ang2 - ang1
        set temp random temp
        set fin_angle (ang1 + temp)
      ]
      [ ;; if ang2 < ang1 
        let temp (360 - ang1) + ang2
        set temp random temp
        set fin_angle (ang1 + temp)
      ]
    ]
  ]
  
  report fin_angle
end

to java-set-random-heading [angle1 angle2]  ;; sweep from angle1 to angle2, clockwise!
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    let current-agent-set agent-set called-set-name
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [
        ask followers 
        [set heading random-heading-range angle1 angle2]
      ]
      if called-set-name = "other"
      [
        ask other-agents 
        [set heading random-heading-range angle1 angle2]
      ]
    ]
    [
      ask [agents] of current-agent-set
      [set heading random-heading-range angle1 angle2]
    ]
  ]
end

to java-set-step-size [ amount ]
  if amount < 0
  [set amount 0]
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    ask called-set
    [
      set set-bonus-speed amount
    ]
  ]
end

to java-step-size-plus [amount]
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    ask called-set
    [set set-bonus-speed set-bonus-speed + amount]
  ]
end

to java-step-size-minus [amount]
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    ask called-set
    [
      set set-bonus-speed set-bonus-speed - amount
      if set-bonus-speed < 0
      [set set-bonus-speed 0]
    ]
  ]
end

to java-reset-forward-values
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    ask called-set
    [set set-bonus-speed 0]
  ]
end

to java-pen-up
 pen-up
end

to java-pen-down
  pen-down
end

to java-stamp
  stamp
end

to java-set-shape [aShape]
  ; "square" "circle" "arrow" are allowed shapes.
  
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [ 
    let current-agent-set agent-set called-set-name
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [
        ask followers 
        [set shape aShape]
      ]
      if called-set-name = "other"
      [
        ask other-agents 
        [set shape aShape]
      ]
    ]
    [
      ask [agents] of current-agent-set
      [set shape aShape]
    ]
  ]
end

to java-go-invisible
  ; ht
  if size != 0
  [set old-size size]
  set size 0
end

to java-go-visible
  if size = 0
  [set size old-size]
end

to set-color-plus [amount]
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    let current-agent-set agent-set called-set-name
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [
        ask followers 
        [set-my-color "plus" amount]
      ]
      if called-set-name = "other"
      [
        ask other-agents 
        [set-my-color "plus" amount]
      ]
    ]
    [
      ask [agents] of current-agent-set
      [set-my-color "plus" amount]
    ]
  ]
end

to set-color-minus [amount]
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    let current-agent-set agent-set called-set-name
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [
        ask followers 
        [set-my-color "minus" amount]
      ]
      if called-set-name = "other"
      [
        ask other-agents 
        [set-my-color "minus" amount]
      ]
    ]
    [
      ask [agents] of current-agent-set
      [set-my-color "minus" amount]
    ]
  ]
end

;to java-set-xy [aX aY]
;  setxy aX aY
;end

to java-set-group-color
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    let current-agent-set agent-set called-set-name
    let total-red 0
    let total-green 0
    let total-blue 0
    
    let agent-count 0
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [
        ask followers 
        [
          let my-rgb extract-rgb color
          set total-red total-red + item 0 my-rgb
          set total-green total-green + item 1 my-rgb
          set total-blue total-blue + item 2 my-rgb
        ]
        set agent-count count followers
      ]
      if called-set-name = "other"
      [
        ask other-agents 
        [
          let my-rgb extract-rgb color
          set total-red total-red + item 0 my-rgb
          set total-green total-green + item 1 my-rgb
          set total-blue total-blue + item 2 my-rgb
        ]
        set agent-count count other-agents
      ]
    ]
    [
      ask [agents] of current-agent-set
      [
        let my-rgb extract-rgb color
        set total-red total-red + item 0 my-rgb
        set total-green total-green + item 1 my-rgb
        set total-blue total-blue + item 2 my-rgb
      ]
      set agent-count count [agents] of current-agent-set
    ]
    
    if agent-count = 0
    [stop]
    
    let mean-red total-red / agent-count
    let mean-green total-green / agent-count
    let mean-blue total-blue / agent-count
    let netlogo-color approximate-rgb mean-red mean-green mean-blue
    
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [
        ask followers 
        [
          set old-color netlogo-color
          set color netlogo-color
        ]
      ]
      if called-set-name = "other"
      [
        ask other-agents 
        [
          set old-color netlogo-color
          set color netlogo-color
        ]
      ]
    ]
    [
      ask [agents] of current-agent-set
      [
        set old-color netlogo-color
        set color netlogo-color
      ]
    ]
  ]
end

to set-my-color [opname value]
  let temp-color color
  if opname = "equal to" 
  [set temp-color value mod 140]
  if opname = "plus" 
  [set temp-color (color + value) mod 140]
  if opname = "minus" 
  [set temp-color (color - value) mod 140]
  if opname = "times" 
  [set temp-color (color * value) mod 140]
  if opname = "divided by"
  [
    if value != 0
    [set temp-color color / value]
  ]
  if opname = "random up to" 
  [set temp-color (random value) mod 140]
  set color temp-color
  set old-color temp-color
end

to java-set-color [opname value]
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    let current-agent-set agent-set called-set-name
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [
        ask followers 
        [
          set-my-color opname value
        ]
      ]
      if called-set-name = "other"
      [
        ask other-agents 
        [
          set-my-color opname value
        ]
      ]
    ]
    [
      ask [agents] of current-agent-set
      [
          set-my-color opname value
      ]
    ]
  ]
end

to set-pen-width-plus [amount]
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    let my-pen-width [set-pen-width] of called-set
    java-pen-width my-pen-width + amount
  ]
end

to set-pen-width-minus [amount]
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    let my-pen-width [set-pen-width] of called-set
    java-pen-width my-pen-width - amount
  ]
end

to java-pen-width [a-width]
  if a-width < 0
  [set a-width 0]
  let called-set one-of set-datas with [set-name = called-set-name]
  if called-set != nobody
  [
    ask called-set
    [
      set set-pen-width a-width
    ]
    
    let current-agent-set agent-set called-set-name
    ifelse current-agent-set = false
    [
      if called-set-name = "all"
      [ask followers [set pen-size a-width]]
      if called-set-name = "other"
      [ask other-agents [set pen-size a-width]]
    ]
    [
      ask [agents] of current-agent-set
      [set pen-size a-width]
    ]
  ]
end

to highlight
  set old-color color
  set color green
end

to unhighlight
  set color old-color
end

to java-Update-set-ellipse [a-set-name shape-list]
  create-set-if-needed a-set-name
  
  let leftX item 0 shape-list
  let topY item 1 shape-list
  let rightX item 2 shape-list
  let bottomY item 3 shape-list
  
  let new-set agent-set a-set-name
  if new-set != false
  [
    ask new-set
    [
      ; Given: topY, bottomY, leftX, rightX coordinates of bounding rectangle.
      ; For any point on the ellipse, sum of distances from the two foci = longSide length of bounding rectangle.
      ; Distance from center of bounding rectangle to either focus = sqrt(longSide^2 - shortSide^2) / 2.
      ; A point is contained within the ellipse if the sum of its distances from each focus <= longSide.
      
      let width rightX - leftX
      let height topY - bottomY

      let cX leftX + width / 2
      let cY bottomY + height / 2

      ifelse width > height
      [
        ; width > height. axis between foci is horizontal.
        let focus-offset sqrt(width * width - height * height) / 2
        let f1X cX - focus-offset
        let f2X cX + focus-offset
    
        set agents (turtle-set followers with 
        [
          xcor >= leftX
          and xcor <= rightX
          and ycor >= bottomY
          and ycor <= topY
          and distancexy-no-wrap f1X cY + distancexy-no-wrap f2X cY <= width
        ])
      ]
      [
        ; width <= height. axis between foci is vertical.
        let focus-offset sqrt(height * height - width * width) / 2
        let f1Y cY - focus-offset
        let f2Y cY + focus-offset
        
        set agents (turtle-set followers with 
        [
          xcor >= leftX
          and xcor <= rightX
          and ycor >= bottomY
          and ycor <= topY
          and distancexy-no-wrap cX f1Y + distancexy-no-wrap cX f2Y <= height
        ])
      ]
    ]
  ]
  
  update-other-agents
end

to-report distancexy-no-wrap [ax ay]
  let x-diff xcor - ax
  let y-diff ycor - ay
  report sqrt(x-diff * x-diff + y-diff * y-diff)
end

to create-set-if-needed [a-set-name]
  if not list-contains-name a-set-name
  [
    create-my-agent-sets 1
    [
      set name a-set-name
      set agents nobody
      ht
    ]
    set my-agent-sets-list lput max-one-of my-agent-sets [who] my-agent-sets-list
  ]
end

to java-Update-set-slice [a-set-name xPos]
  create-set-if-needed a-set-name
  
  let new-set agent-set a-set-name
  let slice-width 10
  if new-set != false
  [
      ask new-set
      [set agents (turtle-set followers with [xcor >= xPos and xcor <= xPos + slice-width])]
  ]
  
  update-other-agents
end

to java-Update-set-color [a-set-name my-color]
  create-set-if-needed a-set-name
  
  let new-set agent-set a-set-name
  if new-set != false
  [
    ifelse my-color = "red"
    [
      ask new-set
      [set agents (turtle-set followers with [(color > 10 and color < 40) or (color > 120)])]
    ]
    [
      ifelse my-color = "green"
      [
        ask new-set
        [set agents (turtle-set followers with [color > 50 and color < 80])]
      ]
      [
        ; my-color = "blue"
        ask new-set
        [set agents (turtle-set followers with [color > 80 and color < 120])]
      ]
    ]
  ]
  
  update-other-agents
end

to java-Update-set-rectangle [a-set-name shape-list]
  create-set-if-needed a-set-name
  
  let left-x item 0 shape-list
  let top-y item 1 shape-list
  let right-x item 2 shape-list
  let bottom-y item 3 shape-list
  
  let new-set agent-set a-set-name
  if new-set != false
  [
    ask new-set
    [set agents (turtle-set followers with [xcor >= left-x and ycor <= top-y and xcor <= right-x and ycor >= bottom-y])]
  ]
  
  update-other-agents
end

to update-other-agents
  set other-agents nobody
  ask followers
  [
    let should-add true
    foreach my-agent-sets-list
    [
      if should-add and member? self [agents] of ?
      [set should-add false]
    ]
    
    if should-add
    [set other-agents (turtle-set other-agents self)]
  ]
end

to-report agent-set [a-set-name]
  foreach my-agent-sets-list
  [
    if [name] of ? = a-set-name
    [report ?]
  ]
  
  report false
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

to-report set-type-arg-list-length [set-type-index]
  report length [arg-list] of item set-type-index set-types-list
end

to-report property-of-arg-for-set-type [arg-index set-type-index property]
  report [runresult property] of [item arg-index arg-list] of item set-type-index set-types-list
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
  set last-cycle false
  set can-highlight-agents false
  set called-set-name ""
  set measure-points []
  set NaN -9007199254740992
end

to setup-cycle
end

to takedown-cycle
ask followers 
[unhighlight]

ask wabbits
[
  set previous-x xcor
  set previous-y ycor
]
;ask set-datas
;[java-place-measure-point]
end

to java-my-place-measure-point
  let my-measurepoint-creator "observer-measure-point-creator"
  if called-set-name != "" and called-set-name != 0
  [set my-measurepoint-creator called-set-name]
  ask set-datas
  [java-place-measure-point my-measurepoint-creator]
end

to java-place-measure-point [a-set-name]
  let my-set-name set-name
  hatch-measurepoints 1
  [
   ht
   set measure-points lput self measure-points
   set tagentkind my-set-name
   set tcycles count measurepoints with [tagentkind = my-set-name] - 1
   set tcolor [color] of myself
   set todometer [ odometer ] of myself
   set tdistfromlast [ distfromlast ] of myself
   set tspeed [set-bonus-speed] of myself
   ifelse [distfromlast] of myself = NaN or [odistfromlast] of myself = NaN 
   [set taccel NaN]
   [set taccel [ distfromlast - odistfromlast ] of myself]
   set tpenwidth [ set-pen-width ] of myself
   set measurepoint-creator a-set-name
   
   set label-color black
   set label tcycles
  ]
  set odistfromlast distfromlast
  set distfromlast 0
end

to-report is-good-color [my-color]
  let faintness my-color mod 10
  ; exclude grays
  if pcolor < 10
  [report false]
  
  ; red, orange, yellow -- accept up to 8
  if pcolor < 50
  [report faintness < 8]
  
  ; green -- accept up to 7
  if pcolor < 70
  [report faintness < 7]
  
  ; blue, pink, purple -- accept up to 7
  report faintness < 7
end

to-report p-in-bounds
  report pxcor >= min-pxcor and pxcor <= max-pxcor and pycor >= min-pycor and pycor <= max-pycor
end

to color-background-patches
  no-display
  resize-world -100 100 -100 100
  set-patch-size 2
  if not is-string? image-to-import
  [set image-to-import "myimage.jpg"]
  
  if not file-exists? image-to-import
  [
    clear-patches
    ask patches
    [set pcolor white]
    ask followers
    [die]
    set other-agents followers
    display
    stop
  ]
  
  import-pcolors image-to-import
  ask patches
  [
    if p-in-bounds and is-good-color pcolor
    [
      sprout-followers 1 
      [
        set image 1 
        set color pcolor 
        set old-color color
        set bonus-speed 0 
        set size 1 
        set shape "circle"
      ]
    ]
  ]
  
  while [count followers > 10000]
  [
    ask followers [die]
    
    let old-patch-size patch-size
    let old-min-pxcor min-pxcor
    let old-max-pxcor max-pxcor
    let old-min-pycor min-pycor
    let old-max-pycor max-pycor
    resize-world (old-min-pxcor / 2) (old-max-pxcor / 2) (old-min-pycor / 2) (old-max-pycor / 2) 
    set-patch-size old-patch-size * 2
    import-pcolors image-to-import
    
    ask patches
    [
      if p-in-bounds and is-good-color pcolor
      [
        sprout-followers 1 
        [
          set image 1 
          set color pcolor 
          set old-color color
          set bonus-speed 0 
          set size 1 
          set shape "circle"
        ]
      ]
    ]
  ]
  
  clear-patches   
  ask patches
  [set pcolor white]
       
  display
  set other-agents followers
end

to make-other-stuff
  create-wabbits 1
       [setxy  random-xcor random-ycor
        set heading 0
        set color green
        set shape "turtle"
        set agent-kind-string "controller"
        ht   ;; hide the turtle so that it is invisible.  
       ]
  ask wabbits
       [set size 30
        set pen-size 3
        set pen-was-down false ;; i.e., pen is up now
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
    
      let datarep (list who tcolor (word "\"" tagentkind "\"") tcycles todometer tdistfromlast tspeed taccel tpenwidth) 
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
      
      let datarep (list who tcolor (word "\"" tagentkind "\"") tcycles todometer tdistfromlast tspeed taccel tpenwidth) 
      set result lput datarep result 
    ]
  ]
  report result
end

to-report get-measures-for-filtered [an-agent-kind a-measurepoint-creator]
  let result []
  let relevant-measures measurepoints with [ tagentkind = an-agent-kind and measurepoint-creator = a-measurepoint-creator ]
  let relevant-list sort relevant-measures
  foreach relevant-list 
  [
    ask ? 
    [ 
      if (is-string? tdistfromlast) 
      [ set tdistfromlast 0 ]
      
      let datarep (list who tcolor (word "\"" tagentkind "\"") (length result + 1) todometer tdistfromlast tspeed taccel tpenwidth) 
      set result lput datarep result 
    ]
  ]
  report result
end
@#$#@#$#@
GRAPHICS-WINDOW
10
10
422
443
100
100
2.0
1
10
1
1
1
0
1
1
1
-100
100
-100
100
0
0
1
ticks
30.0

@#$#@#$#@
## WHAT IS IT?

This section could give a general understanding of what the model is trying to show or explain.

## HOW IT WORKS

This section could explain what rules the agents use to create the overall behavior of the model.

## HOW TO USE IT

This section could explain how to use the model, including a description of each of the items in the interface tab.

## THINGS TO NOTICE

This section could give some ideas of things for the user to notice while running the model.

## THINGS TO TRY

This section could give some ideas of things for the user to try to do (move sliders, switches, etc.) with the model.

## EXTENDING THE MODEL

This section could give some ideas of things to add or change in the procedures tab to make the model more complicated, detailed, accurate, etc.

## NETLOGO FEATURES

This section could point out any especially interesting or unusual features of NetLogo that the model makes use of, particularly in the Procedures tab.  It might also point out places where workarounds were needed because of missing features.

## RELATED MODELS

This section could give the names of models in the NetLogo Models Library or elsewhere which are of related interest.

## CREDITS AND REFERENCES

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
NetLogo 5.1.0
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
default
0.0
-0.2 0 0.0 1.0
0.0 1 1.0 0.0
0.2 0 0.0 1.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180

@#$#@#$#@
0
@#$#@#$#@
