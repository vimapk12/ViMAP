breed [wabbits wabbit]
breed [followers follower]
breed [blocks block]
breed [set-types set-type]
breed [sensors sensor]
breed [agent-kinds kind]
breed [args arg]
breed [my-agent-sets my-agent-set]
breed [ measurepoints measurepoint ]
breed [set-datas set-data]

wabbits-own [pen-was-down ;; if true, the pen was down before the wabbit wrapped around the screen. Only used for wrapping around (so you can
                  ;; put the pen back down after the turtle wraps around, if and only if it was down before.
             agent-kind-string
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
followers-own
[
  coop-same
  coop-different
  PTR
  old-color
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
set-datas-own
[
  set-name
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
measurepoints-own [
 tticks
 tcycles
 tcc
 tcd
 tdc
 tdd
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
    
    last-cycle
    
    other-agents
    
    cost-of-giving
    gain-of-receiving
    death-rate
    initial-PTR
    
    immigrants-per-day
    immigrant-chance-coop-same
    immigrant-chance-coop-different
    mutation-rate
    
    measure-points
    measure-option-string-lists
    measure-option-command-list
    var-name-list
    
    called-set-name
    
    can-highlight-agents
   ]

;;;;;;;;;;;;;;;;;;;;;

to setup ;; sets up the screen
  clear-all
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
  color-background-patches
  create-measure-option-string-lists
  create-measure-option-command-list
  create-var-name-list
  hide-invisibles
  set measure-points []
  create-measure-option-string-lists
  create-measure-option-command-list
  create-var-name-list
  
  reset-ticks    ;; creates ticks and initializes them to 0
end

to create-measure-option-string-lists
  set measure-option-string-lists []
  let tlist []
  set tlist lput "Populations" tlist
  set tlist lput "by Strategy" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists 
  
  set tlist lput "CC" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists
end

to create-var-name-list
  set var-name-list
  [
    "CC"
    "CD"
    "DC"
    "DD"
  ]
end

to-report get-measures
  let result []
  foreach measure-points 
  [
    ask ? 
    [ 
      let datarep (list who green "\"followers\"" tcycles tcc tcd tdc tdd) 
      set result lput datarep result 
    ]
  ]
  report result
end

to-report get-measures-for [an-agent-kind]
  report get-measures
end

to-report get-measures-for-filtered [an-agent-kind a-filter]
  report get-measures
end

to create-measure-option-command-list
  set measure-option-command-list []
  set measure-option-command-list lput "set graph-type \"multi-line\" set ind-var-index 3 set dep-var-index 4 graph" measure-option-command-list
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 4 graph" measure-option-command-list
end

to hide-invisibles
  ask blocks [ht]
  ask set-types [ht]
  ask sensors [ht]
  ask agent-kinds [ht]
  ask args [ht]
  ask my-agent-sets [ht]
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
  ]
end

to create-my-agent-sets-list
  set my-agent-sets-list []
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

to cycle-ended
  ask wabbits
  [
    set previous-x xcor
    set previous-y ycor
  ]
end

to create-predicate-list
  set predicate-list [
  ]
  
 set set-predicate-list [
    "coop-same-color" 
    "coop-different-color" 
    "random-<-energy"
    "random-<-death-rate"
 ]
end

; similar to create-predicate-list
to create-comp-int-list
  set comp-int-left-vars []
end

; similar to create-predicate-list
to create-comp-vars-lists
  set comp-vars-left-vars []
  set comp-vars-right-vars []
end

to-report coop-same-color
  report coop-same
end

to-report coop-different-color
  report coop-different
end

to-report random-<-energy
  report random-float 1 < PTR
end

to-report random-<-death-rate
  report random-float 1 < death-rate
end

to java-die
  die
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
      set block-name "set-immigration-rate"
      set category "Movement"
      set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 1
      set max-value 10
      set min-value 0
    ]
    set arg-list lput max-one-of args [who] arg-list
    
      set is-observer false
      set is-basic true
    ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  
  create-blocks 1
    [
      set block-name "set"
      set category "Movement"
      set arg-list []
          hatch-args 1
    [
      set arg-type "enum"
      set enum-list ["cost-of-giving" "gain-of-receiving" "mutation-rate"]
    ]
    set arg-list lput max-one-of args [who] arg-list
    hatch-args 1
    [
      set arg-type "double"
      set default-value 0.01
      set max-value 0.05
      set min-value 0.00
    ]
    set arg-list lput max-one-of args [who] arg-list
    
      set is-observer false
      set is-basic true
    ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  
    create-blocks 1
    [
      set block-name "same-neighbors-gain-energy"
      set category "Movement"
      set arg-list []
      set is-observer false
      set is-basic true
    ]
    set blocks-list lput max-one-of blocks [who] blocks-list
    
        create-blocks 1
    [
      set block-name "diff-neighbors-gain-energy"
      set category "Movement"
      set arg-list []
      set is-observer false
      set is-basic true
    ]
    set blocks-list lput max-one-of blocks [who] blocks-list
    
    create-blocks 1
    [
      set block-name "lose-energy"
      set category "Movement"
      set arg-list []
      hatch-args 1
    [
      set arg-type "enum"
      set enum-list ["same-neighbor-count" "diff-neighbor-count"]
    ]
    set arg-list lput max-one-of args [who] arg-list
      set is-observer false
      set is-basic true
    ]
    set blocks-list lput max-one-of blocks [who] blocks-list
  
  create-blocks 1
    [
      set block-name "reset-energy"
      set category "Movement"
      set arg-list []
      set is-observer false
      set is-basic true
    ]
    set blocks-list lput max-one-of blocks [who] blocks-list
 
  create-blocks 1
    [
      set block-name "offspring"
      set category "Movement"
      set arg-list []
      set is-observer false
      set is-basic true
    ]
    set blocks-list lput max-one-of blocks [who] blocks-list
    
    create-blocks 1
    [
      set block-name "offspring-with-mutation"
      set category "Movement"
      set arg-list []
      set is-observer false
      set is-basic true
    ]
    set blocks-list lput max-one-of blocks [who] blocks-list
    
    create-blocks 1
    [
      set block-name "setup-full"
      set category "Movement"
      set arg-list []
      set is-observer true
      set is-basic true
    ]
    set blocks-list lput max-one-of blocks [who] blocks-list
    
        create-blocks 1
    [
      set block-name "setup-empty"
      set category "Movement"
      set arg-list []
      set is-observer true
      set is-basic true
    ]
    set blocks-list lput max-one-of blocks [who] blocks-list
    
     create-blocks 1
    [
      set block-name "die"
      set category "Movement"
      set arg-list []
      set is-observer false
      set is-basic true
    ]
    set blocks-list lput max-one-of blocks [who] blocks-list
end

to-report get-agent-kinds-as-csv
  let retn ""
  foreach agent-kind-list
  [
   set retn (word retn ([name] of ?) "," )
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
    set primitives-list lput "setup-full" primitives-list
    set primitives-list lput "setup-empty" primitives-list
    set primitives-list lput "die" primitives-list
    set primitives-list lput "offspring" primitives-list
    set primitives-list lput "offspring-with-mutation" primitives-list
    set primitives-list lput "reset-energy" primitives-list
    set primitives-list lput "same-neighbors-gain-energy" primitives-list
    set primitives-list lput "diff-neighbors-gain-energy" primitives-list
    set primitives-list lput "lose-energy" primitives-list
    set primitives-list lput "set" primitives-list
    set primitives-list lput "set-immigration-rate" primitives-list
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

to java-setup-full
  ask followers [die]
  ask patches [patch-create-follower]
end

to java-setup-empty
  ask followers [die]
end

to java-reset-energy
  set PTR initial-PTR
end

to java-same-neighbors-gain-energy
  let my-color color
  let same-neighbors (followers-on neighbors4) with [color = my-color]
  ask same-neighbors [set PTR PTR + gain-of-receiving]
end

to java-diff-neighbors-gain-energy
  let my-color color
  let diff-neighbors (followers-on neighbors4) with [color != my-color]
  ask diff-neighbors [set PTR PTR + gain-of-receiving]
end

to java-lose-energy [a-agentcount]
  let my-count 0
  let my-color color
  if a-agentcount = "same-neighbor-count"
  [set my-count count (followers-on neighbors4) with [color = my-color]]
  if a-agentcount = "diff-neighbor-count"
  [set my-count count (followers-on neighbors4) with [color != my-color]]
  set PTR PTR - cost-of-giving * my-count
end

to patch-create-follower
    sprout-followers 1 [
    set color random-color
    set coop-same (random-float 1.0 < immigrant-chance-coop-same)
    set coop-different (random-float 1.0 < immigrant-chance-coop-different)
    set old-color color
    update-shape
  ]
end

to-report random-color
  report one-of [yellow brown orange magenta]
end

to update-shape
  ifelse coop-same [
    ifelse coop-different
      [ set shape "circle" ]    ;; filled in circle (altruist)
      [ set shape "circle 2" ]  ;; empty circle (ethnocentric)
  ]
  [
    ifelse coop-different
      [ set shape "square" ]    ;; filled in square (cosmopolitan)
      [ set shape "square 2" ]  ;; empty square (egoist)
  ]
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


to highlight
  set old-color color
  set color green
end

to unhighlight
  set color old-color
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

to java-face [xcoord ycoord]
 facexy xcoord ycoord
end

to java-set-immigration-rate [my-value]
  set immigrants-per-day my-value
end

to java-set [my-var my-value]
  if my-var = "cost-of-giving"
  [set cost-of-giving my-value]

  if my-var = "gain-of-receiving"
  [set gain-of-receiving my-value]
  
  if my-var = "mutation-rate"
  [set mutation-rate my-value]
end

to java-offspring
  let empty-neighbor one-of neighbors4 with [not any? followers-here]
  if empty-neighbor != nobody
  [
    hatch-followers 1
    [move-to empty-neighbor]
  ]
end

to java-offspring-with-mutation
  let empty-neighbor one-of neighbors4 with [not any? followers-here]
  if empty-neighbor != nobody
  [
    hatch-followers 1
    [  
      move-to empty-neighbor
      if random-float 1.0 < mutation-rate 
      [
        let my-old-color color
        while [color = my-old-color]
        [ set color random-color ]
      ]
      if random-float 1.0 < mutation-rate
      [set coop-same not coop-same]
      if random-float 1.0 < mutation-rate
      [set coop-different not coop-different]
      update-shape
    ]
  ]
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
      
  set immigrants-per-day 1
  set immigrant-chance-coop-same 0.5
  set immigrant-chance-coop-different 0.5
  set mutation-rate 0.005
  set cost-of-giving 0.01
  set gain-of-receiving 0.03
  set death-rate 0.1
  set initial-PTR 0.12
  
  set can-highlight-agents false
end

to setup-cycle
end

to takedown-cycle
ask followers 
[unhighlight]
immigrate
place-measure-point
end

to create-set-data [a-set-name]
end

to place-measure-point
  create-measurepoints 1
  [
    ht
    set measure-points lput self measure-points
    set tcycles count measurepoints - 1
    set tcc count followers with [coop-same and coop-different]
    set tcd count followers with [coop-same and not coop-different]

    set tdc count followers with [(not coop-same) and coop-different]
    set tdd count followers with [(not coop-same) and not coop-different]
  ]
end

to immigrate
  let empty-patches patches with [not any? followers-here]
  let how-many min list immigrants-per-day (count empty-patches)
  ask n-of how-many empty-patches [ patch-create-follower ]
end

to-report p-in-bounds
  report pxcor >= min-pxcor and pxcor <= max-pxcor and pycor >= min-pycor and pycor <= max-pycor
end

to color-background-patches
  clear-patches   
  ask patches
  [set pcolor black]
       
  set other-agents followers
end

to make-other-stuff
  create-wabbits 1
       [setxy  random-xcor random-ycor
        set heading 0
        set color green
        set shape "turtle"
        set agent-kind-string "controller"
        ht
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
       
  ask followers [die]
  ask patches [patch-create-follower]
end
@#$#@#$#@
GRAPHICS-WINDOW
6
10
357
382
-1
-1
11.0
1
12
1
1
1
0
1
1
1
0
30
0
30
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
