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
breed [agent-kinds agent-kind]
breed [args arg]
breed [flowers flower]
breed [birds bird]
breed [measurepoints measurepoint]
breed [deads dead]

measurepoints-own 
[ 
  tagentkind
  tcolor
  tcycles
  t-energy-avg
  t-population
  t-proboscis-avg
  t-watched-energy
  measurepoint-creator
  


 tpopulation 
 antEnergy
 theading 
 todometer 
 tdistfromlast 
 tspeed 
 taccel 
 tpenwidth 
 tpencolor 
  
 spiderpopulation
 frogpopulation
]

flowers-own
[
  has-nectar
  is-long
  old-color
  countdown
]

wabbits-own 
[
  pen-was-down ;; if true, the pen was down before the wabbit wrapped around the screen. Only used for wrapping around (so you can
               ;; put the pen back down after the turtle wraps around, if and only if it was down before.
  agent-kind-string
  flag-counter ;; counts the number of flags/markers that have been dropped so far in the run
  
  energy
  is-long-beak
  forward-distance
  odometer            
     distfromlast        
     odistfromlast       
     
     secret-number   
     repeat-num

     
     bonus-speed 
     initial-x
     initial-y
     previous-x
     previous-y
     
     has-food
     paint-color
     
     age
     of-preference
     skin-hydrocarbon
]

deads-own
[countdown]

birds-own
[
    bird-index ;; used to prevent all birds from turning to face the same butterfly (the one with maximal color-distance from flower-color).
               ;; birds are immortal and indexed from 1-n. the bird with index i would turn to face the butterfly with ith-greatest 
               ;; color-distance from flower-color
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

globals
[
  future-x ;; tells what the wabbit's x-coordinate would be if it went forward the given distance
  showLable?
  number-of-steps
  x-for-draw ;; used for drawing numbers
  y-for-draw ;; used for drawing numbers
  color-for-draw
  
  
  ;;NEEDED FOR MEASURE LINKING
  measure-points
  
  eat-radius
  
  first-flag
  second-flag
  flag-distance
  
  wabbits-list
  wabbit-kind-list
  blocks-list ;; list of blocks used to populate the toolbars / palettes in Java construction-world
  sensors-list ;; list of sensors for Java construction-world
  agent-kind-list ;; list of agent-kind in the model (breeds)
  predicate-list
  chart-data-name-list
  comp-int-left-vars
  comp-vars-left-vars
  comp-vars-right-vars
  
  can-highlight-agents
  
  ; list of category names for blocks
  categories-list
  
  current-window-max-size
  current-window
  this-cycle-current
  
  measure-option-string-lists
  measure-option-command-list
  
  var-name-list
  
  flower-color
  reproduction-rate
  
  short-flower-label
  long-flower-label
                       
  last-cycle
  
  dead-duration
  butterfly-size
  baby-size
  
  flower-count
  flower-layout ; "random", "cluster", "long-short", or "long-short-both"
  
  called-set-name
    
    NaN
]

patches-own
[
  is-food
  is-ant-nest
  is-flying-ant-nest
  
  is-depleted
  pheromone-scent
  ppaint-color   
]

;;;;;;;;;;;;;;;;;;;;;

to setup ;; sets up the screen
  clear-all
  ask patches [set is-food false set is-ant-nest false set is-flying-ant-nest false]
  set-defaults
  clear-drawing
  color-background-patches
  
  ;;NEEDED FOR MEASURE LINKING
  set measure-points []
    make-observer
  make-other-stuff
  create-chart-data-name-list
  create-agent-kind-list
  create-blocks-list
  create-sensors-list
  create-predicate-list
  create-comp-int-list
  create-comp-vars-lists
  create-categories-list
  create-measure-option-string-lists
  create-measure-option-command-list
  create-var-name-list
  
  highlight-animation
  prepare-change-energy
  prepare-lose-energy-forward-dist
  prepare-set-speed
  prepare-drink-nectar
  prepare-set-butterfly-color
  prepare-watch-random-butterfly
  prepare-set-flower-count
  prepare-set-flowers
  prepare-face-food-if-hungry
  prepare-random-turn
  prepare-die
  prepare-go-forward
  prepare-make-same-color-baby
  prepare-make-similar-color-baby
  prepare-set-proboscis
  
prepare-set-step-size
prepare-omnivory
prepare-place-pheromone
prepare-show-energy-counter
prepare-hide-energy-counter
prepare-go-forward
prepare-eat-ant
prepare-eat-flying-ant
prepare-eat-spider
prepare-create-food
prepare-reproduce-ant
prepare-eat-pollinator
prepare-eat-frog
prepare-eat-snake
prepare-eat-bird
prepare-face-friend
prepare-agent-die
prepare-reproduce-flying-ant
prepare-reproduce-unicorn
prepare-reproduce-spider
prepare-reproduce-frog
prepare-reproduce-snake
prepare-reproduce-bird
prepare-reproduce-pollinator
prepare-face-pheromone
prepare-face-ant-nest
prepare-face-flying-ant-nest
prepare-pickup-food-here
prepare-random-turn
prepare-right
prepare-left
prepare-add-energy
prepare-lose-energy
prepare-dropoff-food-here
set showLable? true
ask blocks [ht]       
ask args [ht]
ask agent-kinds [ht]
  reset-ticks    ;; creates ticks and initializes them to 0
end

to create-labels
  create-turtles 1
  [
     setxy -15 -20
     set color green
     set size 0
  ]
  set short-flower-label max-one-of turtles [who]
  
  create-turtles 1
  [
     setxy -15 -23
     set color green
     set size 0
  ]
  set long-flower-label max-one-of turtles [who]
  
  update-labels
end


to update-labels
  ask short-flower-label
  [set label (word "Short Flowers: " short-flowers)]
  ask long-flower-label
  [set label (word "Long Flowers: " long-flowers)]
end

to-report short-flowers
  report count flowers with [not is-long]
end

to-report long-flowers
  report count flowers with [is-long]
end

to create-var-name-list
  set var-name-list
  [
    "avg butterfly energy" 
    "butterfly population" 
    "avg proboscis length"
    "watched butterfly energy"
  ]
end

to setup-cycle
end

to takedown-cycle
  cycle-ended
  place-measure-point
  
  if not any? wabbits with [agent-kind-string = "butterflies"]
  [set last-cycle true]
  diffuse pheromone-scent 0.5 
  ask patches 
  [ 
    ;set ppaint-color pcolor
    set pheromone-scent pheromone-scent * 0.9
   if showLable? = true [ask wabbits [set label energy]]
    recolor-patch 
  ]
end

to recolor-patch  ;; patch procedure
  ;; give color to nest and food sources
  if not is-ant-nest and not is-food and not is-flying-ant-nest   ;; adjust last condition to reduce 
  [ 
    let a_color scale-color green pheromone-scent 0.1 5
    if a_color < 55
    [ set a_color 55 ]
    set pcolor a_color
  ]
   
end

to create-categories-list
  set categories-list [ "Control" "Movement" "Drawing" "Sensors"  "Secret Number"]
end

to create-chart-data-name-list
  set chart-data-name-list 
  [
    "distance-from-start" 
    "distance-from-previous"
    ]
end


to cycle-ended
  bird-cycle
  ask wabbits with [agent-kind-string != "butterflies"]
  [
    set previous-x xcor
    set previous-y ycor
  ]
 
  ask wabbits with [agent-kind-string = "butterflies"]
  [
    ; set energy energy - 1
    set label energy
    
    if energy <= 0
    [java-die]
    
    if size < butterfly-size
    [set size size + 0.1]
  ]
  
  ask flowers
  [
    if countdown != 0
    [
      set countdown countdown - 1
      if countdown = 0
      [
        get-nectar
      ]
    ]
  ]
  
  ask deads
  [
    set countdown countdown - 1
    if countdown < 0
    [die]
  ]
   tick
end

to bird-cycle
  ask birds
  [
    let chase false
    let target nobody
    if any? wabbits with [agent-kind-string = "butterflies"]
    [
      ; face the "bird-index"th greatest butterfly by color-distance from flower-color
      let butterflies sort-by [[new-color-distance] of ?1 > [new-color-distance] of ?2] (wabbits with [agent-kind-string = "butterflies"])
      
      ifelse length butterflies > bird-index - 1
      [set target item (bird-index - 1) butterflies]
      [set target last butterflies]
      
      let dist [new-color-distance] of target
     ; this is the old, more lenient code
     ; ifelse dist > 220
     ; [set chase true]
     ; [
     ;   ifelse dist > 180 and random 10 < 7
     ;   [set chase true]
     ;   [
     ;     ifelse dist > 140 and random 10 < 5
     ;     [set chase true]
     ;     [
     ;       if dist > 100 and random 10 < 2
     ;       [set chase true] 
     ;     ]
     ;   ]
     ; ]      
      
      ifelse dist > 180
      [set chase true]
      [
        ifelse dist > 140 and random 10 < 7
        [set chase true]
        [
          ifelse dist > 100 and random 10 < 5
          [set chase true]
          [
            ifelse dist > 60 and random 10 < 3
            [set chase true]
            [
              if dist > 40 and random 10 < 2
              [set chase true]
            ]
          ]
        ]
      ]
      
      ifelse chase
      [face target]
      [
        if random 10 < 3
        [
          right random 10
          left 10
        ]
      ]
    ]
    
    forward 3
    
    if chase
    [
      let nearby-radius 3
      if distance target <= nearby-radius
      [
        ask target
        [die]
      ]
    ]
  ]
end

to create-predicate-list
  set predicate-list ["nectar-here" "can-reproduce" "no-energy" "There-is-food-here" "I-am-holding-food" "at-ant-nest" "at-flying-ant-nest"  
    "I-have-50-energy" "I-have-0-energy" "the-chance-is-50-out-of-100" "the-chance-is-10-out-of-100" "the-chance-is-1-out-of-100" 
    "at-ant" "at-flying-ant" "at-pollinator" "at-spider" "at-frog" "at-snake" "at-bird" "at-unicorn" "I-have-100-energy" 
    "I-have-200-energy" "I-have-500-energy"  "I-have-1000-energy" "patch-matches-preference" ]
end

; similar to create-predicate-list
to create-comp-int-list
  set comp-int-left-vars ["heading" "step-size"]
end

; similar to create-predicate-list
to create-comp-vars-lists
  set comp-vars-left-vars ["heading" "step-size"]
  set comp-vars-right-vars ["heading" "step-size"]
end

to-report java-There-is-food-here [aWho]
  let food-radius 2
  let result false
  if turtle aWho != nobody [
  ask turtle aWho
  [
  ;  set result [is-food] of patch-here
  set result any? patches in-radius food-radius with [is-food]
  ]]
  report result
end

to-report java-at-ant-nest [aWho]
  let result false
  if turtle aWho != nobody [
  ask turtle aWho
  [
    set result [is-ant-nest] of patch-here
  ]]
  report result
end

to-report java-at-flying-ant-nest [aWho]
  let result false
  if turtle aWho != nobody [
  ask turtle aWho
  [
    set result [is-flying-ant-nest] of patch-here
  ]]
  report result
end

to-report java-at-friend [aWho]
  let fradius 2
  let result false
  if turtle aWho != nobody [
  ask turtle aWho
  [
  set result any? wabbits in-radius fradius with [agent-kind-string = [agent-kind-string] of myself]
  ]]
  report result
end

to-report java-I-am-holding-food [aWho]
  let result false
  if turtle aWho != nobody [
  ask turtle aWho
  [
    set result has-food
  ]]
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

to-report java-patch-matches-preference [aWho]
 let result false
  ask turtle aWho[
   if shade-of? paint-color [ppaint-color] of patch-here = true [set result true]
  ]
  report result
end

to-report java-the-chance-is-50-out-of-100 [aWho]
  let result false
  if turtle aWho != nobody [
  ask turtle aWho[
    if random 100 <= 50 = true [set result true]
  ]]
  report result
end



to-report java-the-chance-is-10-out-of-100 [aWho]
  let result false
 if turtle aWho != nobody [
  ask turtle aWho[
    if random 100 <= 10 = true [set result true]
  ]
  ]
  report result
end

to-report java-the-chance-is-1-out-of-100 [aWho]
  let result false
 if turtle aWho != nobody [
  ask turtle aWho[
    if random 100 <= 1 = true [set result true]
  ]
  ]
  report result
end

to-report java-I-have-50-energy [aWho]
  let result false
  if turtle aWho != nobody [
  ask turtle aWho[
    if energy > 50 = true [set result true]
  ]
  ]
  report result
end

to-report java-I-have-100-energy [aWho]
  let result false
  if turtle aWho != nobody [
  ask turtle aWho[
    if energy > 50 = true [set result true]
  ]
  ]
  report result
end

to-report java-I-have-200-energy [aWho]
  let result false
  if turtle aWho != nobody [
  ask turtle aWho[
    if energy > 50 = true [set result true]
  ]
  ]
  report result
end

to-report java-I-have-500-energy [aWho]
  let result false
  if turtle aWho != nobody [
  ask turtle aWho[
    if energy > 50 = true [set result true]
  ]
  ]
  report result
end

to-report java-I-have-1000-energy [aWho]
  let result false
  if turtle aWho != nobody [
  ask turtle aWho[
    if energy > 50 = true [set result true]
  ]
  ]
  report result
end

to-report java-I-have-0-energy [aWho]
  let result false
  if turtle aWho != nobody [
  ask turtle aWho[
    if  energy <= 0 = true [set result true]
  ]
  ]
  report result
end

to-report java-at-ant [aWho]
  let fradius 2
  let result false
  if turtle aWho != nobody [
   ; if [agent-kind-string] of turtle aWho = "ant" or [agent-kind-string] of turtle aWho = "flying-ant" [
   ; set fradius 8]
  ask turtle aWho
  [
  set result any? wabbits in-radius fradius with [agent-kind-string = "ant"]
  ]]
  report result
end

to-report java-at-flying-ant [aWho]
  let fradius 2
  let result false
  if turtle aWho != nobody [
  ask turtle aWho
  [
  set result any? wabbits in-radius fradius with [agent-kind-string = "flying-ant"]
  ]]
  report result
end

to-report java-at-pollinator [aWho]
  let fradius 2
  let result false
  if turtle aWho != nobody [
  ask turtle aWho
  [
  set result any? wabbits in-radius fradius with [agent-kind-string = "pollinator"]
  ]]
  report result
end

to-report java-at-spider [aWho]
  let fradius 2
  let result false
  if turtle aWho != nobody [
  ask turtle aWho
  [
  set result any? wabbits in-radius fradius with [agent-kind-string = "spider"]
  ]]
  report result
end

to-report java-at-frog [aWho]
  let fradius 2
  let result false
  if turtle aWho != nobody [
  ask turtle aWho
  [
  set result any? wabbits in-radius fradius with [agent-kind-string = "frog"]
  ]]
  report result
end

to-report java-at-snake [aWho]
  let fradius 2
  let result false
  if turtle aWho != nobody [
  ask turtle aWho
  [
  set result any? wabbits in-radius fradius with [agent-kind-string = "snake"]
  ]]
  report result
end

to-report java-at-bird [aWho]
  let fradius 2
  let result false
  if turtle aWho != nobody [
  ask turtle aWho
  [
  set result any? wabbits in-radius fradius with [agent-kind-string = "bird"]
  ]]
  report result
end

to-report java-at-unicorn [aWho]
  let fradius 2
  let result false
  if turtle aWho != nobody [
  ask turtle aWho
  [
  set result any? wabbits in-radius fradius with [agent-kind-string = "unicorn"]
  ]]
  report result
end

to-report java-no-energy [aWho]
  if not any? wabbits with [who = aWho]
  [report false]
  if [agent-kind-string] of wabbit aWho != "butterflies"
  [report true]
  
  let result false
  ask wabbit aWho
  [set result energy <= 0]
  report result
end

to-report java-can-reproduce [aWho]  
  if not any? wabbits with [who = aWho]
  [report false]
  if [agent-kind-string] of wabbit aWho != "butterflies"
  [report false]
  
  let min-energy 5
  let result false
  ask wabbit aWho
  [set result energy > min-energy]
  report result
end

to-report java-heading [aWho]
  let result 0
  ask turtle aWho
  [set result heading]
  report result
end

to-report java-nectar-here [aWho]
  if not any? wabbits with [who = aWho]
  [report false]
  if [agent-kind-string] of wabbit aWho != "butterflies"
  [report false]
  
  let result false
  ask wabbit aWho
  [
    if any? flowers in-radius eat-radius with [has-nectar and not is-long]
    [set result true]
    
    if is-long-beak and any? flowers in-radius eat-radius with [has-nectar]
    [set result true]
  ]
  
  report result
end

to-report java-true [aWho]
  report true
end

to-report java-false [aWho]
  report false
end

to-report java-color-red [aWho]
  report [color] of turtle aWho = red
end

to-report java-past-halfway [aWho]
  report [xcor] of turtle aWho > 200
end

to reset
setup
end

to create-sensors-list
  set sensors-list []
end

to create-blocks-list
  set blocks-list []
  ask blocks [ht]
  ask args [ht]
end

to create-agent-kind-list
  set agent-kind-list [] 
  
    ;;;;; PUT AGENT TYPE DEFINITIONS HERE: ;;;;;
    
  
  create-agent-kinds 1
  [
    set name "butterflies"
    
    set methods-list []
    set methods-list lput "go" methods-list
     set methods-list lput "setup" methods-list
    
    set primitives-list []
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
 
 create-agent-kinds 1
  [
    set name "observer"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
   
   create-agent-kinds 1
  [
    set name "ant"
    
    set methods-list []
    set methods-list lput "go" methods-list
    set methods-list lput "setup" methods-list

    set primitives-list []    
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
  
  create-agent-kinds 1
  [
    set name "flying-ant"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []

  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
   create-agent-kinds 1
  [
    set name "pollinator"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []

  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
  create-agent-kinds 1
  [
    set name "queen-ant"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []

  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
  create-agent-kinds 1
  [
    set name "queen-flying-ant"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []

  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list

create-agent-kinds 1
  [
    set name "spider"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []

  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
  create-agent-kinds 1
  [
    set name "frog"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []

  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
  create-agent-kinds 1
  [
    set name "snake"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []

  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
  create-agent-kinds 1
  [
    set name "bird"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []

          
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
  create-agent-kinds 1
  [
    set name "unicorn"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []

     
  ]
 set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
 
  ;;;;; END OF AGENT TYPE DEFINITIONS ;;;;;
  ask agent-kinds [ht]
end

to-report get-agent-kinds-as-csv
  let retn ""
  foreach agent-kind-list
  [
   set retn (word retn ([name] of ?) "," )
  ]
  report butlast retn
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


;to-report get-list-as-csv [list-name]
;  let retn ""
;  foreach list-name
;  [
;   set retn (word retn ? "," )
;  ]
;  report butlast retn
;end


to create-measure-option-string-lists
  set measure-option-string-lists []
  
  let tlist []
  set tlist lput "Average Energy" tlist
  set tlist lput "of Butterflies" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists
  
  set tlist []
  set tlist lput "Total Number" tlist
  set tlist lput "of Butterflies" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists
  
  set tlist []
  set tlist lput "Average" tlist 
  set tlist lput "Proboscis" tlist
  set tlist lput "Length" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists
  
  set tlist []
  set tlist lput "Energy" tlist
  set tlist lput "of Watched" tlist
  set tlist lput "Butterfly" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists
  
  
  set tlist lput "Population" tlist
  set tlist lput "of Ants" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists
  
  set tlist []
  set tlist lput "Average Energy" tlist
  set tlist lput "of Ants" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists
  
  set tlist []
  set tlist lput "Population" tlist
  set tlist lput "of Spiders" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists
 
  set tlist []
  set tlist lput "Population" tlist
  set tlist lput "of Frogs" tlist
  set measure-option-string-lists lput tlist measure-option-string-lists


end

to create-measure-option-command-list
  set measure-option-command-list []
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 4 graph" measure-option-command-list
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 5 graph" measure-option-command-list
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 6 graph" measure-option-command-list
  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 7 graph" measure-option-command-list
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
        set result result / (count wabbits with [ agent-kind-string = current-agent-kind] )
        set result precision result 1
      ]
    ]
  ]
  
  report result
end
;to create-measure-option-command-list
;  set measure-option-command-list []
;  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 4 graph" measure-option-command-list
;  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 5 graph" measure-option-command-list
;  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 6 graph" measure-option-command-list
;  set measure-option-command-list lput "set graph-type \"horizontal-lineup-height\" set ind-var-index 3 set dep-var-index 7 graph" measure-option-command-list
; 
; 
;end
; graph-index is the index of the graph in measure-option-command-list and other lists
to highlight-agents-for-graph [graph-index a-agent-type]
  unhighlight-agents
end


to unhighlight-agents
end

; possibly remove.
; indirection primitive to take the place of "ask".
; does not throw an error if turtle a-who does not exist.
; example usage:
;    instead of:
;     ask turtle 1000 [forward 5] ; throws error if no turtle 1000
;    use:
;    do 1000 "forward 5" ; does nothing if no turtle 1000
to do [a-who command]
  if turtle a-who != nobody
  [
    ask turtle a-who
    [run command]
  ]
end

to highlight-animation
  unhighlight-agents
end

to-report random-color
  report rgb (random 256) (random 256) (random 256)
end

; deprecated. use new-color-distance instead.
; butterfly reporter
;to-report color-distance
;  let red-dist abs ((item 0 color) - (item 0 flower-color))
;  let green-dist abs ((item 1 color) - (item 1 flower-color))
;  let blue-dist abs ((item 2 color) - (item 2 flower-color))
;  report (red-dist + green-dist + blue-dist) / 3
;end

;***************************Create Vimap Block*****************************************
to prepare-place-pheromone
   create-blocks 1
  [
    set block-name "place-pheromone"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
;Add it to the named agents' list of Vimap primitives
 ask agent-kinds with [name = "unicorn" or name = "ant"]   
 [set primitives-list lput "place-pheromone" primitives-list]  
end

;define the functionality
to java-place-pheromone
  set pheromone-scent pheromone-scent + 60
end
;**************************************************************************************

;created by Ashlyn!!
;***************************Create Vimap Block*****************************************
to prepare-face-friend
 create-blocks 1
  [
    set block-name "face-friend"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "unicorn" or name = "ant" or name = "flying-ant"]   
 [set primitives-list lput "face-friend" primitives-list]   
end
;define the function      
to java-face-friend
  let fradius 2
  if any? wabbits in-radius fradius with [agent-kind-string = [agent-kind-string] of myself]
  [
    ;ask one-of wabbits in-radius fradius with [agent-kind-string = "ant"]
  let friend one-of wabbits in-radius fradius with [agent-kind-string = [agent-kind-string] of myself ]
  if friend != nobody [
  let friend-x [xcor] of friend
  let friend-y [ycor] of friend
  facexy friend-x friend-y]]
end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-pickup-food-here
   create-blocks 1
  [
    set block-name "pickup-food-here"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
  
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "pollinator" or name = "unicorn" 
   or name = "ant" or name = "flying-ant"]   
 [set primitives-list lput "pickup-food-here" primitives-list]    
end

;define the function  
to java-pickup-food-here
  set has-food true
  set shape "ant-has-food"
  let food-radius 2
  if any? patches in-radius food-radius with [is-food]
  [
    ask one-of patches in-radius food-radius with [is-food]
    [
      set is-food false
      set is-depleted true
      let a_color (random 6) + 32  ;; a_color gets a random shade of brown
      set pcolor a_color
      set ppaint-color a_color
    ]
  ] 
end
;****************************************************************************************

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
;************************************************************************************  
  
;***************************Create Vimap Block*****************************************  
  to prepare-hide-energy-counter
         create-blocks 1
  [
    set block-name "hide-energy-counter"
    set category "secret-number"
    set arg-list []
    set is-observer false
    set is-basic false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      ;Add it to the agents' lists
 ask agent-kinds with [name = "unicorn" or name = "ant"]   
 [set primitives-list lput "hide-energy-counter" primitives-list]
    set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
end
      ;create the function      
to java-hide-energy-counter
  set label ""
  set showLable? false
end

;************************************************************************************ 
    
;***************************Create Vimap Block*****************************************
  to prepare-show-energy-counter
         create-blocks 1
  [
    set block-name "show-energy-counter"
    set category "secret-number"
    set arg-list []
    set is-observer false
    set is-basic false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
;Add it to the agents' lists
 ask agent-kinds with [name = "unicorn" or name = "ant"]   
 [set primitives-list lput "show-energy-counter" primitives-list]
    set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
end
      ;create the function
to java-show-energy-counter
  set label energy
  set showLable? true
end
;************************************************************************************  



;***************************Create Vimap Block*****************************************
to prepare-set-step-size 
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
    ht
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list

;Add it to the agents list
 ask agent-kinds with [name = "unicorn" or name = "ant"]   
 [set primitives-list lput "set-step-size" primitives-list]
    set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
end

;define the function
to java-set-step-size [ aspeed ]
  set bonus-speed aspeed
  if (aspeed < 0) [ set bonus-speed 0 ]
end
;**********************************************************************************************************
;to-report java-heading [aWho]
;  let result 0
;  ask turtle aWho
;  [set result heading]
;  report result
;end

to-report java-step-size [aWho]
  let result 0
  ask turtle aWho
  [set result bonus-speed]
  report result
end
;***************************Create Vimap Block*****************************************
to prepare-go-forward
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
;Add it to the agents' list
 ask agent-kinds with [name = "unicorn" or name = "ant" or name = "pollinator" or name = "spider"
   or name = "snake" or name = "bird" or name = "flying-ant" or name = "frog" or name = "butterflies"]   
 [set primitives-list lput "go-forward" primitives-list]
    set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
end
;define functionality
to java-go-forward 
  if agent-kind-string != "butterflies" [jump bonus-speed]
   if agent-kind-string = "butterflies" [jump forward-distance]
  ask wabbits with [agent-kind-string = "flying-ant"][
  ifelse shape = "flying-ant1" [set shape "flying-ant2"]
  [set shape "flying-ant1"]
  ]
end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-right
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
    set label-after-arg " degrees"
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
  
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "unicorn" or name = "ant" or name = "pollinator"
    or name = "spider" or name = "snake" or name = "bird" 
    or name = "flying-ant" or name = "frog" ]   
 [set primitives-list lput "right" primitives-list]    
end

;define the function      
to java-right [amount-number]
  right amount-number
end
;****************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-left
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
    
    set label-after-arg " degrees"
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
  
 ;Add it to the named agents' primitives list
 ask agent-kinds with [
   name = "unicorn" or name = "ant" or name = "pollinator"
    or name = "spider" or name = "snake" or name = "bird" 
    or name = "flying-ant" or name = "frog"  ]   
 [set primitives-list lput "left" primitives-list]    
end

;define the function  
to java-left [amount-number]
  left amount-number
end
;****************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-random-turn
               create-blocks 1
  [
    set block-name "random-turn"
    set category "Movement"
    
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 60
      set max-value 180
      set min-value 0
    ]
    set label-after-arg " degrees"
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
  
 ;Add it to the named agents' primitives list
 ask agent-kinds with [
   name = "pollinator" or name = "unicorn" 
   or name = "ant" or name = "flying-ant" or name = "spider" 
   or name = "frog" or name = "snake" or name = "bird" or name = "butterflies"
    ]   
 [set primitives-list lput "random-turn" primitives-list]    
end
to java-random-turn [amount-number]
  let rand (random-float amount-number * 2)
  right rand - amount-number
end
;****************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-face-pheromone
        create-blocks 1
  [
    set block-name "face-pheromone"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
  
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "pollinator" or name = "unicorn" 
   or name = "ant" or name = "flying-ant" or name = "spider"]   
 [set primitives-list lput "face-pheromone" primitives-list]    
end

;define the function 
to java-face-pheromone
  if (pheromone-scent >= 0.05) and (pheromone-scent < 2)
[
  let scent-ahead pheromone-scent-at-angle 0
  let scent-right pheromone-scent-at-angle 45
  let scent-left pheromone-scent-at-angle -45
  if (scent-right > scent-ahead) or (scent-left > scent-ahead)
  [ 
    ifelse scent-right > scent-left
    [rt 45]
    [lt 45] 
  ]
]
end
;****************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-lose-energy
    create-blocks 1
  [
    set block-name "lose-energy"
    set is-observer false
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 1
      set max-value 1010
      set min-value 0
    ]
    set arg-list lput max-one-of args [who] arg-list
    set category "Movement"
    set is-basic true
    ; other variables not applicable
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
      
  
 ;Add it to the named agents' primitives list
 ask agent-kinds with [
   name = "unicorn" or name = "ant" or name = "pollinator"
    or name = "spider" or name = "snake" or name = "bird" 
    or name = "flying-ant" or name = "frog" or name = "queen-ant"
    or name = "queen-flying-ant"  ]   
 [set primitives-list lput "lose-energy" primitives-list]    
end

;define the function  
to java-lose-energy [lostEnergy]
  set energy energy - lostEnergy
end
;****************************************************************************************

to-report pheromone-scent-at-angle [angle]
  let p patch-right-and-ahead angle 1
  if p = nobody [ report 0 ]
  report [pheromone-scent] of p
end

;***************************Create Vimap Block*****************************************
to prepare-eat-ant
       create-blocks 1
  [
    set block-name "eat-ant"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "unicorn" or name = "ant"]   
 [set primitives-list lput "eat-ant" primitives-list]
    
end
;define the function      
to java-eat-ant
  let food-radius 2
  if any? wabbits in-radius food-radius with [agent-kind-string  = "ant"]
  [let prey one-of wabbits in-radius food-radius with [agent-kind-string = "ant"  ] 
    if prey != nobody
    [if [energy] of prey > 0 
      [set energy energy + [energy] of prey]
      ask prey [die]    ]
  ]  
end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-eat-flying-ant
       create-blocks 1
  [
    set block-name "eat-flying-ant"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the agents' lists
 ask agent-kinds with [name = "unicorn" or name = "spider"]   
 [set primitives-list lput "eat-flying-ant" primitives-list]
    
end
;define the function
to java-eat-flying-ant
   let food-radius 2
  if any? wabbits in-radius food-radius with [agent-kind-string  = "flying-ant"]
  [let prey one-of wabbits in-radius food-radius with [agent-kind-string = "flying-ant"  ]
    
    if prey != nobody
    [if [energy] of prey > 0 
      [set energy energy + [energy] of prey]
      ask prey [die]    ]
  ]
   
end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-eat-spider
       create-blocks 1
  [
    set block-name "eat-spider"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "unicorn" or name = "snake"]   
 [set primitives-list lput "eat-spider" primitives-list]
    
end
;define the function
to java-eat-spider
   let food-radius 2
  if any? wabbits in-radius food-radius with [agent-kind-string  = "spider"]
  [let prey one-of wabbits in-radius food-radius with [agent-kind-string = "spider"  ]
    
    if prey != nobody
    [if [energy] of prey > 0 
      [set energy energy + [energy] of prey]
      ask prey [die]    ]
  ]
   
end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-eat-frog
       create-blocks 1
  [
    set block-name "eat-frog"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "unicorn" or name = "snake" or name = "bird"]   
 [set primitives-list lput "eat-frog" primitives-list]
    
end
;define the function 
to java-eat-frog
   let food-radius 2
  if any? wabbits in-radius food-radius with [agent-kind-string  = "frog"]
  [let prey one-of wabbits in-radius food-radius with [agent-kind-string = "frog"  ]
    
    if prey != nobody
    [if [energy] of prey > 0 
      [set energy energy + ( [energy] of prey)] 
      ask prey [die]    ]
  ]
    
end
;*************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-eat-snake
      create-blocks 1
  [
    set block-name "eat-snake"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "unicorn" or name = "bird"]   
 [set primitives-list lput "eat-snake" primitives-list]
    
end
;define the function 
to java-eat-snake
   let food-radius 2
  if any? wabbits in-radius food-radius with [agent-kind-string  = "snake"]
  [let prey one-of wabbits in-radius food-radius with [agent-kind-string = "snake"  ]
    
    if prey != nobody
    [if [energy] of prey > 0 
      [set energy energy + [energy] of prey]
      ask prey [die]    ]
  ]
   
end
;*************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-eat-bird
       create-blocks 1
  [
    set block-name "eat-bird"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "unicorn" or name = "snake"]   
 [set primitives-list lput "eat-bird" primitives-list]
    
end
;define the function      
to java-eat-bird
    let food-radius 2
  if any? wabbits in-radius food-radius with [agent-kind-string  = "bird"]
  [let prey one-of wabbits in-radius food-radius with [agent-kind-string = "bird"  ]
    
    if prey != nobody
    [if [energy] of prey > 0 
      [set energy energy + [energy] of prey]
      ask prey [die]    ]
  ]
    
end
;*************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-eat-pollinator
       create-blocks 1
  [
    set block-name "eat-pollinator"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "unicorn" or name = "ant" or name = "bird"]   
 [set primitives-list lput "eat-pollinator" primitives-list]
    
end
;define the function      

to java-eat-pollinator
   let food-radius 2
  if any? wabbits in-radius food-radius with [agent-kind-string  = "pollinator"]
  [let prey one-of wabbits in-radius food-radius with [agent-kind-string = "pollinator"  ]
    
    if prey != nobody
    [if [energy] of prey > 0 
      [set energy energy + [energy] of prey]
      ask prey [die]    ]
  ]
   
end
;****************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-face-ant-nest
 create-blocks 1
  [
    set block-name "face-ant-nest"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
  
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "pollinator" or name = "unicorn" 
   or name = "ant" or name = "flying-ant" or name = "spider"]   
 [set primitives-list lput "face-ant-nest" primitives-list]    
end

;define the function  
to java-face-ant-nest
  facexy 0 0
end
;****************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-face-flying-ant-nest
    create-blocks 1
  [
    set block-name "face-flying-ant-nest"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
  
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "pollinator" or name = "unicorn" 
   or name = "ant" or name = "flying-ant" or name = "spider"]   
 [set primitives-list lput "face-flying-ant-nest" primitives-list]    
end

;define the function
to java-face-flying-ant-nest
  facexy -40 -40
end
;****************************************************************************************
;end of ant methods
;begin butterfly methods
;***************************Create Vimap Block*****************************************
to prepare-set-speed
   create-blocks 1
  [
    set block-name "set-speed"
    set arg-list []
    hatch-args 1
    [
       set arg-type "int"
       set default-value 1
       set max-value 5
       set min-value 1
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    set is-basic false
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "butterflies"]   
 [set primitives-list lput "set-speed" primitives-list]
    
end
;define the function
to java-set-speed [value]
  set forward-distance value
end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-lose-energy-forward-dist
  create-blocks 1
  [
    set block-name "lose-energy-forward-dist"
    set arg-list []
    set is-observer false
    set is-basic false
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "butterflies"]   
 [set primitives-list lput "lose-energy-forward-dist" primitives-list]
    
end
;define the function
to java-lose-energy-forward-dist
  set energy energy - forward-distance
end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-change-energy
  create-blocks 1
  [
    set block-name "change-energy"
    set arg-list []
    hatch-args 1
    [
      set arg-type "enum"
      set enum-list ["plus" "minus"]
    ]
    set arg-list lput max-one-of args [who] arg-list
    hatch-args 1
    [
       set arg-type "int"
       set default-value 1
       ; set max-value 5 ; this value is used without the lose-energy-forward-dist block
       set max-value 15
       set min-value 1
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer false
    set is-basic false
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "butterflies"]   
 [set primitives-list lput "change-energy" primitives-list]
    
end
;define the function
to java-change-energy [operator-name change-value]
  if operator-name = "plus"
  [set energy energy + change-value]
  if operator-name = "minus"
  [set energy energy - change-value]
end
;**************************************************************************************

; butterfly procedure
;to java-lose-energy
;  set energy energy - 1
;end

; butterfly procedure
;to java-get-energy
;  set energy energy + 5
;end

; butterfly procedure

;***************************Create Vimap Block*****************************************
to prepare-drink-nectar
   create-blocks 1
  [
    set block-name "drink-nectar"
    set is-observer false
    set arg-list []
    set is-basic false
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "butterflies"]   
 [set primitives-list lput "drink-nectar" primitives-list]
    
end
;define the function
to java-drink-nectar
  ; let gain-from-eat 2
  
  ifelse is-long-beak
  [
    if any? flowers in-radius eat-radius with [has-nectar]
    [
      let target min-one-of flowers with [has-nectar] [distance myself]
      ask target
      [lose-nectar]
      ; set energy energy + gain-from-eat
    ]
  ]
  [
    if any? flowers in-radius eat-radius with [has-nectar and not is-long]
    [
      let target min-one-of flowers with [has-nectar and not is-long] [distance myself]
      ask target
      [lose-nectar]
      ; set energy energy + gain-from-eat
    ]
  ]
end
;**************************************************************************************


; flower procedure
to lose-nectar
  if has-nectar
  [
    set old-color color
    set has-nectar false
    reduce-rgb-color
    set countdown 2
  ]
end

; flower procedure
to get-nectar
  if not has-nectar
  [
    set has-nectar true
    set color old-color
  ]
end

to reduce-rgb-color
  let r reduce-color-value item 0 color
  let g reduce-color-value item 1 color
  let b reduce-color-value item 2 color
  set color rgb r g b
end

to-report reduce-color-value [value]
  let delta 40
  let result 0
  if value - delta > 0
  [set result value - delta]
  report result
end
;***************************Create Vimap Block*****************************************
;to prepare-random-turn butterfly random turn
;create-blocks 1
;  [
;    set block-name "random-turn"
;    set is-observer false
;    set is-basic true
;    set arg-list []
;    set category "Movement"
;    ; other variables not applicable
;  ]
;  set blocks-list lput max-one-of blocks [who] blocks-list
; ;Add it to the named agents' primitives list
; ask agent-kinds with [name = "butterflies"]   
; [set primitives-list lput "random-turn" primitives-list]   
;end
;;define the function
;to java-random-turn
;  let max-turn 30
;  right ((random (max-turn + 1)) - max-turn / 2)
;end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-die
create-blocks 1
  [
    set block-name "die"
    set is-observer false
    set is-basic true
    set arg-list []
    ; other variables not applicable
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "butterflies"]   
 [set primitives-list lput "die" primitives-list]
end
;define the function
to java-die
  let long-beak is-long-beak
  
  hatch-deads 1
  [
    ifelse long-beak
    [set shape "butterfly-long"]
    [set shape "butterfly"]
    set color black
    set countdown dead-duration
  ]
  
  die
end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
;to prepare-go-forward
;create-blocks 1
;  [
;    set block-name "go-forward"
;    set is-observer false
;    set is-basic true
;    set arg-list []
;    set category "Movement"
;    ; other variables not applicable
;  ]
;  set blocks-list lput max-one-of blocks [who] blocks-list
; ;Add it to the named agents' primitives list
; ask agent-kinds with [name = "butterflies"]   
; [set primitives-list lput "go-forward" primitives-list]
;    
;end
;;define the function
;to java-go-forward
;  jump forward-distance
;end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-face-food-if-hungry
create-blocks 1
    [
    set block-name "face-food-if-hungry"
    set is-observer false
    set is-basic false
    set arg-list []
    set category "Movement"
    ; other variables not applicable
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "butterflies"]   
 [set primitives-list lput "face-food-if-hungry" primitives-list]
    
end
;define the function
; butterfly primitive
to java-face-food-if-hungry
  ;let hungry-energy random 12 ; this value was used before butterflies lost energy proportional to their speed
  let hungry-energy random 36 ; use if butterflies lose their speed in energy each tick
  if energy <= hungry-energy
  [
    ifelse is-long-beak
    [
      if any? flowers with [has-nectar]
      [face min-one-of flowers with [has-nectar] [distance myself]]
    ]
    [
      if any? flowers with [has-nectar and not is-long]
      [face min-one-of flowers with [has-nectar and not is-long] [distance myself]]
    ]
  ]
end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-set-butterfly-color
    create-blocks 1
  [
    set block-name "set-butterfly-color"
    set is-observer false
    set is-basic true
    set arg-list []
    hatch-args 1
    [
      set arg-type "enum"
      set enum-list ["red" "blue" "random"]
    ]
    set arg-list lput max-one-of args [who] arg-list
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "observer"]   
 [set primitives-list lput "set-butterfly-color" primitives-list]
    
end
;define the function
to java-set-butterfly-color [a-color]
  if a-color = "red"
  [
    ask wabbits with [agent-kind-string = "butterflies"]
    [set color rgb 255 0 0]
  ]
  if a-color = "blue"
  [
    ask wabbits with [agent-kind-string = "butterflies"]
    [set color rgb 0 0 255]
  ]
  if a-color = "random"
  [
    ask wabbits with [agent-kind-string = "butterflies"]
    [
      set color random-color
    ]
  ]
end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-set-proboscis
create-blocks 1
  [
    set block-name "set-proboscis"
    set is-observer false
    set is-basic false
    set arg-list []
    hatch-args 1
    [
      set arg-type "enum"
      set enum-list ["long" "short" "either"]
    ]
    set arg-list lput max-one-of args [who] arg-list
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "observer"]   
 [set primitives-list lput "set-proboscis" primitives-list]
    
end
;define the function
to java-set-proboscis [proboscis]
  if proboscis = "long"
  [
    ask wabbits with [agent-kind-string = "butterflies"]
    [
      set is-long-beak true
      set shape "butterfly-long"
    ]
  ]
  if proboscis = "short"
  [
    ask wabbits with [agent-kind-string = "butterflies"]
    [
      set is-long-beak false
      set shape "butterfly"
    ]
  ]
  if proboscis = "either"
  [
    ask wabbits with [agent-kind-string = "butterflies"]
    [
      set is-long-beak false
      set shape "butterfly"
    ]
    ask n-of (count wabbits with [agent-kind-string = "butterflies"] / 2) wabbits with [agent-kind-string = "butterflies"]
    [
      set is-long-beak true
      set shape "butterfly-long"
    ]
  ]
end
;**************************************************************************************

to update-flowers
  ask flowers
  [die]
  
  create-flowers flower-count
  [
    setxy random-xcor random-ycor
    set color flower-color
    set old-color color
    set size 6
    set shape "flower"
    set is-long false
    set has-nectar true
    set countdown 0
  ]  
  
  update-flower-layout
end

to update-flower-layout
  if flower-layout = "random"
  [
    ask flowers
    [
      setxy random-xcor random-ycor
      set is-long true
      set shape "flower-long"
    ]
    ask n-of (count flowers / 2) flowers
    [
      set is-long false
      set shape "flower"
    ]
  ]
  
  if flower-layout = "cluster"
  [
    ; cluster in upper-right quadrant of screen
    ask flowers
    [
      set xcor random max-pxcor
      set ycor random max-pycor
      set is-long true
      set shape "flower-long"
    ]
    ask n-of (count flowers / 2) flowers
    [
      set is-long false
      set shape "flower"
    ]
  ]
  if flower-layout = "long-short"
  [
    ask flowers
    [
      set xcor random max-pxcor
      set ycor random max-pycor
      set is-long true
      set shape "flower-long"
    ]
    ask n-of (count flowers / 2) flowers
    [
      set xcor -1 * xcor
      set ycor -1 * ycor
      set is-long false
      set shape "flower"
    ]
  ]
  if flower-layout = "long-short-both"
  [
    ; layout: upper 2/3 of left third, middle 2/3 of middle third, lower 2/3 of right third
    let lefts n-of (count flowers / 3) flowers
    let centers n-of (count flowers / 3) flowers with [not member? self lefts]
    let rights flowers with [not member? self lefts and not member? self centers]
    
    let left-third-max-xcor min-pxcor + (world-width / 3)
    let right-third-min-xcor min-pxcor + (world-width * 2 / 3)
    let top-third-min-ycor min-pycor + (world-height * 2 / 3)
    let middle-third-min-ycor min-pycor + (world-height / 3)
    
    ask lefts
    [
      set xcor min-pxcor + random (world-width / 3)
      set ycor top-third-min-ycor + random (world-height / 3)
      set is-long true
      set shape "flower-long"
    ]
    ask centers
    [
      set xcor left-third-max-xcor + random (world-width / 3)
      set ycor middle-third-min-ycor + random (world-height / 3)
      set is-long false
      set shape "flower"
    ]
    ask rights
    [
      set xcor right-third-min-xcor + random (world-width / 3)
      set ycor min-pycor + random (world-height / 3)
      set is-long true
      set shape "flower-long"
    ]
    ask n-of (count rights / 2) rights
    [
      set is-long false
      set shape "flower"
    ]
  ]
  
  update-labels
end

;***************************Create Vimap Block*****************************************
to prepare-set-flower-count
   create-blocks 1
  [
    set block-name "set-flower-count"
    set arg-list []
    set is-basic false
    hatch-args 1
    [
       set arg-type "int"
       set default-value 10
       set max-value 15
       set min-value 8
    ]
    set arg-list lput max-one-of args [who] arg-list
    set is-observer true
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "observer"]   
 [set primitives-list lput "set-flower-count" primitives-list]
    
end
;define the function
to java-set-flower-count [value]
  set flower-count value
  update-flowers
end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-set-flowers
 create-blocks 1
  [
    set block-name "set-flowers"
    set is-observer false
    set is-basic false
    set arg-list []
    hatch-args 1
    [
      set arg-type "enum"
      set enum-list ["random" "cluster" "long-short" "long-short-both"]
    ]
    set arg-list lput max-one-of args [who] arg-list
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "observer"]   
 [set primitives-list lput "set-flowers" primitives-list]
    
end
;define the function
; layout could be "random", "cluster", "long-short", or "long-short-both"
to java-set-flowers [layout]
  set flower-layout layout
  update-flower-layout
end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-watch-random-butterfly
   create-blocks 1
  [
    set block-name "watch-random-butterfly"
    set is-observer true
    set is-basic false
    set arg-list []
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "observer"]   
 [set primitives-list lput "watch-random-butterfly" primitives-list]
    
end
;define the function
to java-watch-random-butterfly
clear-drawing
if any? wabbits with [agent-kind-string = "butterflies"]
[
  watch one-of wabbits with [agent-kind-string = "butterflies"]
  ask subject
  [pen-down]
]
end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-make-similar-color-baby
  create-blocks 1
  [
    set block-name "make-similar-color-baby"
    set is-observer false
    set is-basic false
    set arg-list []
    set category "Movement"
    ; other variables not applicable
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "butterflies"]   
 [set primitives-list lput "make-similar-color-baby" primitives-list]
    
end
;define the function
; butterfly primitive
to java-make-similar-color-baby
  if random 100 < reproduction-rate
  [
    let my-color color
    let my-beak is-long-beak
    let my-forward-distance forward-distance
    hatch-wabbits 1
    [
      set agent-kind-string "butterflies"
      set color new-shifted-color my-color
      set is-long-beak my-beak
      set size baby-size
      set forward-distance my-forward-distance
      pen-up
    ]
  ]
end
;**************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-create-food
   create-blocks 1
  [
    set block-name "create-food"
    set is-observer false
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 50
      set max-value 100
      set min-value 0
    ]
    set label-after-arg " % of the time"
    set arg-list lput max-one-of args [who] arg-list
    set category "Movement"
    set is-basic true
    ; other variables not applicable
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  
   ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "unicorn" or name = "pollinator"]   
 [set primitives-list lput "create-food" primitives-list]
  
end
;define the function
to java-create-food [acolorplaceprob]
  let currentcolor random 139
  if random 100 < acolorplaceprob 
  [ 
    ask [ neighbors ] of patch-here  
    [
      set ppaint-color currentcolor  
      set pcolor currentcolor
      set is-food true
      set is-depleted false
    ]
  ]
end
;******************************************************************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-reproduce-ant
       create-blocks 1
  [
    set block-name "reproduce-ant"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "queen-ant"]   
 [set primitives-list lput "reproduce-ant" primitives-list]
 
end
;define the function
to java-reproduce-ant 
  hatch 1 [
    setxy [xcor] of myself [ycor] of myself
      set heading random 360
       set color red
       set shape "ant"
       set size 3
       set energy ([energy] of myself * 0.5)
       set agent-kind-string "ant"
      set distfromlast NaN
      set odistfromlast NaN
      ]  
end
;****************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-reproduce-flying-ant
       create-blocks 1
  [
    set block-name "reproduce-flying-ant"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "queen-flying-ant"]   
 [set primitives-list lput "reproduce-flying-ant" primitives-list]    
end

;define the function      
to java-reproduce-flying-ant 
  hatch 1 [
    setxy [xcor] of myself [ycor] of myself
      set heading random 360
       set color red
       set shape "flying-ant1"
       set size 3
       set energy ([energy] of myself * 0.5)
       set agent-kind-string "flying-ant"
      set distfromlast NaN
      set odistfromlast NaN
      ]
end
;****************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-reproduce-pollinator
       create-blocks 1
  [
    set block-name "reproduce-pollinator"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "pollinator" or name = "unicorn"]   
 [set primitives-list lput "reproduce-pollinator" primitives-list]    
end

;define the function
to java-reproduce-pollinator 
  hatch 1 [
    setxy [xcor] of myself [ycor] of myself
      set heading random 360
       set color red
       set shape "bug"
       set size 4
       set energy ([energy] of myself * 0.5)
       set agent-kind-string "pollinator"
      set distfromlast NaN
      set odistfromlast NaN
      ]
  
end
;****************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-reproduce-spider
      create-blocks 1
  [
    set block-name "reproduce-spider"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "unicorn" or name = "spider"]   
 [set primitives-list lput "reproduce-spider" primitives-list]    
end

;define the function      
to java-reproduce-spider 
  hatch 1 [
    setxy [xcor] of myself [ycor] of myself
      set heading random 360
      set color grey
       set shape "spider"
       set size 6
       set energy ([energy] of myself * 0.5)
       set agent-kind-string "spider"
      set distfromlast NaN
      set odistfromlast NaN
      ]
end
;****************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-reproduce-frog
    create-blocks 1
  [
    set block-name "reproduce-frog"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "frog" or name = "unicorn"]   
 [set primitives-list lput "reproduce-frog" primitives-list]    
end

;define the function 
to java-reproduce-frog 
  hatch 1 [
    setxy [xcor] of myself [ycor] of myself
      set heading random 360
       set color red
       set shape "frog"
       set size 7
       set energy ([energy] of myself * 0.5)
       set agent-kind-string "frog"
      set distfromlast NaN
      set odistfromlast NaN
      ]
  
end
;****************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-reproduce-snake
        create-blocks 1
  [
    set block-name "reproduce-snake"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "snake" or name = "unicorn"]   
 [set primitives-list lput "reproduce-snake" primitives-list]    
end

;define the function      
to java-reproduce-snake 
  hatch 1 [
    setxy [xcor] of myself [ycor] of myself
      set heading random 360
       set color red
       set shape "snake"
       set size 8
       set energy ([energy] of myself * 0.5)
       set agent-kind-string "snake"
      set distfromlast NaN
      set odistfromlast NaN
      ]
  
end
;****************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-reproduce-bird
    create-blocks 1
  [
    set block-name "reproduce-bird"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "bird" or name = "unicorn"]   
 [set primitives-list lput "reproduce-bird" primitives-list]    
end

;define the function 
to java-reproduce-bird 
  hatch 1 [
    setxy [xcor] of myself [ycor] of myself
      set heading random 360
       set color red
       set shape "bird"
       set size 9
       set energy ([energy] of myself * 0.5)
       set agent-kind-string "bird"
      set distfromlast NaN
      set odistfromlast NaN
      ]
  
end
;****************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-reproduce-unicorn
        create-blocks 1
  [
    set block-name "reproduce-unicorn"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "flying-queen-ant"]   
 [set primitives-list lput "reproduce-unicorn" primitives-list]    
end

;define the function      
to java-reproduce-unicorn 
  hatch 1 [
    setxy [xcor] of myself [ycor] of myself
      set heading random 360
       set color red
       set shape "unicorn"
       set size 10
       set energy ([energy] of myself * 0.5)
       set agent-kind-string "unicorn"
      set distfromlast NaN
      set odistfromlast NaN
      ]
  
end
;****************************************************************************************
  
;***************************Create Vimap Block*****************************************
to prepare-add-energy
    create-blocks 1
  [
    set block-name "add-energy"
    set is-observer false
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 1
      set max-value 1010
      set min-value 0
    ]
    set arg-list lput max-one-of args [who] arg-list
    set category "Movement"
    set is-basic true
    ; other variables not applicable
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
      
  
 ;Add it to the named agents' primitives list
 ask agent-kinds with [
   name = "unicorn" or name = "ant" or name = "pollinator"
    or name = "spider" or name = "snake" or name = "bird" 
    or name = "flying-ant" or name = "frog" or name = "queen-ant" or name = "queen-flying-ant"  ]   
 [set primitives-list lput "add-energy" primitives-list]    
end

;define the function      
to java-add-energy [addedEnergy]
  set energy energy + addedEnergy
end
;****************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-dropoff-food-here
     create-blocks 1
  [
    set block-name "dropoff-food-here"
    set is-observer false
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 50
      set max-value 101
      set min-value 0
    ]
     set label-after-arg " % of the time"
    set arg-list lput max-one-of args [who] arg-list
    set category "Movement"
    set is-basic true
    ; other variables not applicable
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  
 ;Add it to the named agents' primitives list
 ask agent-kinds with [
   name = "unicorn" or name = "ant" or name = "pollinator"
    or name = "flying-ant" ]   
 [set primitives-list lput "dropoff-food-here" primitives-list]    
end

;define the function      
to java-dropoff-food-here [afoodprob]
   set has-food false
   set shape "ant"
   if random 100 < afoodprob 
   [
     set is-food true
     set is-depleted false
   ]
end
;****************************************************************************************

;***************************Create Vimap Block*****************************************
to prepare-agent-die
      create-blocks 1
  [
    set block-name "agent-die"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "unicorn" or name = "snake"or name = "ant" or name = "flying-ant"
   or name = "spider" or name = "snake" or name = "frog" or name = "bird" or name = "queen-ant" 
   or name = "flying-queen-ant"]   
 [set primitives-list lput "agent-die" primitives-list]  
end

;define the function      
to java-agent-die
  die
end
;*************************************************************************************


;***************************Create Vimap Block***************************************************
to prepare-Omnivory
 create-blocks 1
  [
    set block-name "Omnivory"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
;Add it to the named agents' primitives lists
 ask agent-kinds with [name = "unicorn" or name = "ant"]   
 [set primitives-list lput "Omnivory" primitives-list]
    set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
end
;define the function
to java-Omnivory
  let food-radius 2
  if any? wabbits in-radius food-radius with [agent-kind-string != [agent-kind-string] of myself]
  [let prey one-of wabbits in-radius food-radius with [agent-kind-string != [agent-kind-string] of myself and agent-kind-string != "queen-ant" and agent-kind-string != "queen-flying-ant" ]
    
    if prey != nobody
    [if [energy] of prey > 0 
      [set energy energy + [energy] of prey]
      ask prey [die]    ]
  ] 
end
;*************************************************************************************
to-report new-shifted-color [start-color]
  let result shifted-color start-color 80
  while [rgb-to-delta-e start-color result > 40]
    [show rgb-to-delta-e start-color result set result shifted-color start-color 40]
  report result
end

; do not use directly; is based on non-uniform RGB color model.
; call through new-shifted-color [start-color].
to-report shifted-color [my-old-color delta-max]
  let new-red shifted-color-element (item 0 my-old-color) delta-max
  let new-green shifted-color-element (item 1 my-old-color) delta-max
  let new-blue shifted-color-element (item 2 my-old-color) delta-max
  report rgb new-red new-green new-blue
end  

to-report shifted-color-element [old-color-element delta-max]
  let result old-color-element + (random (delta-max + 1)) - delta-max / 2
  set result min (list 255 result)
  set result max (list 0 result)
  report result
end

;***************************Create Vimap Block*****************************************
to prepare-make-same-color-baby
 create-blocks 1
  [
    set block-name "make-same-color-baby"
    set is-observer false
    set is-basic false
    set arg-list []
    set category "Movement"
    ; other variables not applicable
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
 ;Add it to the named agents' primitives list
 ask agent-kinds with [name = "butterflies"]   
 [set primitives-list lput "make-same-color-baby" primitives-list]
    
end
;define the function
; butterfly primitive
to java-make-same-color-baby
  if random 100 < reproduction-rate
  [
    let my-color color
    let my-beak is-long-beak
    let my-forward-distance forward-distance
    hatch-wabbits 1
    [
      set agent-kind-string "butterflies"
      set color my-color
      set is-long-beak my-beak
      set size baby-size
      set forward-distance my-forward-distance
      pen-up
    ]
  ]
end
;**************************************************************************************



;to create-wabbits-list
;  set wabbits-list sort [who] of wabbits
;end



to-report mean-butterfly-energy
  let result 0
  ask wabbits with [agent-kind-string = "butterflies"]
  [set result result + energy]
  report result / count wabbits with [agent-kind-string = "butterflies"]
end

;;NEEDED FOR MEASURE LINKING
to place-measure-point
create-measurepoints 1
  [
    set measure-points lput self measure-points
    set tagentkind "ant"
    set tcycles count measurepoints - 1
    set tpopulation (count wabbits with [agent-kind-string = "ant"])
    if count wabbits with [agent-kind-string = "ant"] > 0 [set antEnergy (mean [energy] of wabbits with  [agent-kind-string = "ant"])]
    set spiderpopulation (count wabbits with [agent-kind-string = "spider"])
    set frogpopulation (count wabbits with [agent-kind-string = "frog"])
    set measurepoint-creator "ant"
    ht
  ]

;  create-measurepoints 1
;  [
;    set measure-points lput self measure-points 
;    set tcycles count measurepoints - 1
;    ifelse any? wabbits with [agent-kind-string = "butterflies"]
;    [
;      set t-energy-avg mean-butterfly-energy
;      set t-proboscis-avg (count wabbits with [agent-kind-string = "butterflies" and is-long-beak]) / (count wabbits with [agent-kind-string = "butterflies"])
;    ]
;    [
;      set t-energy-avg 0
;      set t-proboscis-avg 0
;    ]
;    
;    ifelse subject = nobody
;    [set t-watched-energy 0]
;    [set t-watched-energy [energy] of subject]
;
;    
;    set t-population (count wabbits with [agent-kind-string = "butterflies"])
;    set tagentkind "butterflies"
;    set measurepoint-creator "butterflies"
;    ht
;  ]
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

to-report arg-list-length [block-index]
  report length [ arg-list ] of item block-index blocks-list
end

to-report property-of-arg-for-block [arg-index block-index property]
  report [runresult property] of [item arg-index arg-list] of item block-index blocks-list
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





to-report list-item [list-name index]
  report item index list-name
end

to-report list-item-property [list-name index property]

; runresult converts the String variable into its 
; NetLogo code equivalent (a property name)
  report [runresult property] of item index list-name 
end


to color-background-patches
  ask patches
  [
    set pcolor green
    
    set ppaint-color pcolor
  ]
  create-ant-nest
    create-flying-ant-nest
end

to create-ant-nest
  if count wabbits with [agent-kind-string = "queen-ant"] = 1 [
    ask patches
  [
    
    set is-flying-ant-nest false
    set is-food false
    set is-depleted false
  ]
  ask patches with [distancexy 0 0 < 4]
  [
    set is-ant-nest true
    set pcolor brown
  ]
  ]
  
  
end

to create-flying-ant-nest
  if count wabbits with [agent-kind-string = "flying-queen-ant"] = 1 [
    ask patches
  [
    set is-flying-ant-nest false
    set is-ant-nest false
    set is-food false
    set is-depleted false
  ]
  ask patches with [distancexy -40 -40 < 4]
  [
    set is-flying-ant-nest true
    set pcolor brown
  ]
  
  ]
end








to-report java-is-repeat-var
  report false
end



to set-defaults
  set number-of-steps 0
  set flower-color random-color
  
  ;old value for use with lenient birds
  ;set reproduction-rate 5
  set reproduction-rate 8
  
  set eat-radius 3
  
  set current-window-max-size 20
  set current-window []
  
  set can-highlight-agents false
  set last-cycle false
  
  set dead-duration 3
  set baby-size 2.5
  set butterfly-size 5
  set flower-count 10
  set flower-layout "random"
  set NaN -9007199254740992
   set-default-shape wabbits "circle"
end



to make-other-stuff
  create-wabbits 12
  [
    setxy random-xcor random-ycor
    set agent-kind-string "butterflies"
    set size butterfly-size
    set color random-color
    ; set energy 10 + random 10 ; this value is used without the lose-energy-forward-dist block
    set energy 20 + random 10
    set label energy
    set is-long-beak false
    set shape "butterfly"
    set forward-distance 3
  ]
  create-wabbits 1
  [
    set agent-kind-string "observer"
    ht
  ]
  ask wabbits
  [
    set pen-was-down false ;; i.e., pen is up now
  ]
  
  create-birds 1
  [
    set bird-index 1
  ]
  create-birds 1
  [
    set bird-index 2
  ]
  ask birds
  [
    setxy random-xcor random-ycor
    set size 9
    set shape "bird side"
    set color blue
  ]

  create-labels
  update-flowers
end

to make-observer
  create-wabbits 10
        [setxy random-xcor random-ycor
        set heading random 360
        set color red
        set shape "ant"
        set size 3
        set energy 100
        set agent-kind-string "ant"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
       ]
  create-wabbits 10
        [setxy random-xcor random-ycor
        set heading random 360
        set color blue
        set shape "flying-ant1"
        set size 3
        set energy 100
        set agent-kind-string "flying-ant"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
       ]
       create-wabbits 1
        [setxy 25 25
        set heading 90
        set color blue
        set paint-color color
        set size 4
        set shape "bug"
        set agent-kind-string "pollinator"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
       ]
        
     create-wabbits 1
        [setxy -25 -25
        set heading 90
        set color blue
        set paint-color color
        set size 4
        set shape "bug"
        set agent-kind-string "pollinator"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
       ]
        
      create-wabbits 1
        [setxy 25 -25
        set heading 90
        set color yellow
        set size 4
        set paint-color color
        set shape "squirrel"
        set agent-kind-string "pollinator"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
       ]
        
       create-wabbits 1
        [setxy 0 0
        set heading 90
        set color red
        set size 8
        set paint-color color
        set shape "ant"
        set agent-kind-string "queen-ant"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
       ]
        
          ask patches with [distancexy 0 0 < 4]
  [
    set is-ant-nest true
    set pcolor brown
  ]
        
      create-wabbits 1
        [setxy -40 -40
        set heading 90
        set color red
        set size 8
        set paint-color color
        set shape "flying-ant1"
        set agent-kind-string "queen-flying-ant"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
       ]
        
          ask patches with [distancexy -40 -40 < 4]
  [
    set is-flying-ant-nest true
    set pcolor brown
  ]
        
   create-wabbits 1
        [setxy random-xcor random-ycor
        set heading random 360
        set color grey
        set shape "spider"
        set size 6
        set energy 100
        set agent-kind-string "spider"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
       ]
        
create-wabbits 1
        [setxy random-xcor random-ycor
        set heading random 360
        set color grey
        set shape "frog"
        set size 7
        set energy 100
        set agent-kind-string "frog"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
       ]
        
create-wabbits 1
        [setxy random-xcor random-ycor
        set heading random 360
        set color grey
        set shape "snake"
        set size 8
        set energy 100
        set agent-kind-string "snake"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
       ]
        
  create-wabbits 1
        [setxy random-xcor random-ycor
        set heading random 360
        set color grey
        set shape "bird"
        set size 9
        set energy 100
        set agent-kind-string "bird"
        set distfromlast NaN
        set odistfromlast NaN
       ]
        
  create-wabbits 1
        [setxy random-xcor random-ycor
        set heading random 360
        set color grey
        set shape "unicorn"
        set size 10
        set energy 100
        set agent-kind-string "unicorn"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
       ]
        
        ask wabbits
       [
        set pen-size 5
        
        set has-food false
        ; pen is up now
        set pen-was-down false 
        set bonus-speed 2
        set flag-counter 0
        set label energy
        show label
        
        set secret-number random 101    ;SECRET NUMBER
        set repeat-num random 5 + 2     ;REPEAT NUMBER
        
        set initial-x xcor
        set initial-y ycor
        set previous-x ""
        set previous-y ""
        ;if agent-kind-string != "queen-ant" or agent-kind-string != "pollinator" [set size 3]
       ]
  
  end


to-report get-measures
  let result []
  foreach measure-points 
  [
    ask ? 
    [
      let datarep (list who red (word "\"" tagentkind "\"") tcycles tpopulation antEnergy spiderpopulation frogpopulation theading tdistfromlast tspeed taccel tpenwidth tpencolor) 
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
      let datarep (list who red (word "\"" tagentkind "\"") tcycles tpopulation antEnergy spiderpopulation frogpopulation theading tdistfromlast tspeed taccel tpenwidth tpencolor) 
      set result lput datarep result 
    ]
  ]
  report result
end

to-report get-measures-for-filtered [an-agent-kind a-measurepoint-creator]
  report get-measures-for an-agent-kind
end

; a-color an rgb color, with each value from 0-255
; reports a list (x y z)
; reference: http://www.easyrgb.com/index.php?X=MATH&H=02#text2
to-report xyz-from-rgb [a-color]
  let r (item 0 a-color) / 255
  let g (item 1 a-color) / 255
  let b (item 2 a-color) / 255
  
  ifelse r > 0.4045
  [set r ((r + 0.055) / 1.055) ^ 2.4]
  [set r r / 12.92]

  ifelse g > 0.4045
  [set g ((g + 0.055) / 1.055) ^ 2.4]
  [set g g / 12.92]

  ifelse b > 0.4045
  [set b ((b + 0.055) / 1.055) ^ 2.4]
  [set b b / 12.92]
  
  set r r * 100
  set g g * 100
  set b b * 100

  ; Observer = 2 degrees, Illuminant = D65
  let x r * 0.4124 + g * 0.3576 + b * 0.1805
  let y r * 0.2126 + g * 0.7152 + b * 0.0722
  let z r * 0.0193 + g * 0.1192 + b * 0.9505
  
  report (list x y z)
end

; a-list a CIEXYZ list
; reports a CIELAB color as a list of L, a, b
; reference: http://www.easyrgb.com/index.php?X=MATH&H=07#text7
to-report lab-from-xyz [a-list]
  ; Observer= 2 degrees, Illuminant= D65
  let ref-x 95.047
  let ref-y 100.000
  let ref-z 108.883
  
  let x (item 0 a-list) / ref-x
  let y (item 1 a-list) / ref-y
  let z (item 2 a-list) / ref-z
  
  ifelse x > 0.008856
  [set x x ^ (1 / 3)]
  [set x (7.787 * x) + (16 / 116)]
  
  ifelse y > 0.008856
  [set y y ^ (1 / 3)]
  [set y (7.787 * y) + (16 / 116)]
  
  ifelse z > 0.008856
  [set z z ^ (1 / 3)]
  [set z (7.787 * z) + (16 / 116)]

  let l (116 * y) - 16
  let a 500 * (x - y)
  let b 200 * (y - z)

  report (list l a b)
end

; reports the CIE76 delta-E between two colors in CIELAB color space,
; given as lists of l, a, b coordinates.
; reference: http://en.wikipedia.org/wiki/Color_difference
to-report lab-to-delta-e [lab-color-a lab-color-b]
  report sqrt((item 0 lab-color-a - item 0 lab-color-b) ^ 2 + 
    (item 1 lab-color-a - item 1 lab-color-b) ^ 2 +
    (item 2 lab-color-a - item 2 lab-color-b) ^ 2)
end

; measures color distance from flower color using CIE76 algorithm
; reference: http://en.wikipedia.org/wiki/Color_difference
to-report new-color-distance
  report rgb-to-delta-e color flower-color
end

; reports the perceived color differene between two rgb colors, by
; converting the colors to CIEXYZ and then CIELAB color space,
; then taking the Euclidean distance between them in CIELAB space,
; which is the same as the CIE76 color difference metric.
to-report rgb-to-delta-e [rgb-color-a rgb-color-b]
  report lab-to-delta-e (lab-from-xyz 
    (xyz-from-rgb rgb-color-a)) 
    (lab-from-xyz (xyz-from-rgb rgb-color-b))
end
@#$#@#$#@
GRAPHICS-WINDOW
10
10
428
449
25
25
8.0
1
10
1
1
1
0
1
1
1
-25
25
-25
25
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

bird side
true
0
Polygon -7500403 true true 180 0 210 45 210 75 180 105 180 150 165 240 180 285 165 285 150 300 150 240 135 195 105 255 105 210 90 150 105 90 120 60 165 45
Circle -16777216 true false 188 38 14

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

butterfly-long
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
Polygon -7500403 true true 135 105 150 15 165 105 135 105

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

flower-long
false
0
Polygon -10899396 true false 135 120 165 165 180 210 180 240 150 300 165 300 195 240 195 195 165 135
Circle -7500403 true true 70 117 38
Circle -7500403 true true 177 70 38
Circle -7500403 true true 70 25 38
Circle -7500403 true true 55 70 38
Circle -7500403 true true 115 10 38
Circle -7500403 true true 81 36 108
Circle -16777216 true false 98 53 74
Polygon -10899396 true false 189 233 219 188 249 173 279 188 234 218
Polygon -10899396 true false 180 255 150 210 105 210 75 240 135 240
Circle -7500403 true true 160 25 38
Polygon -7500403 true true 120 135 180 240 195 225 180 120 120 135

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

minus
false
14
Rectangle -1 true false 0 90 300 210

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

plus
false
0
Rectangle -1 true false 105 0 195 300
Rectangle -1 true false 0 105 300 195

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
