                      ; Welcome to the the Real code which runs the ant food grab program. 
                      ; For starters, its very exciting you've chose to see how it works! congrats!
                      ; In the code below, we will explain how each part works using comments,
                      ; comments in programs are pieces meant for humans, instead of computers, to read like you are now.
                      ; a comment in netlogo starts with a ; and continues to the end of the line. 
                      ; Comments also are grey, instead of black or green or purplie.
                      ; color is important in netlogo code. Try to notice the pattern of colors.


                    

breed [wabbits wabbit]                 ; ants, spiders, queen-ants, and polinators are all kinds of wabbits
breed [blocks block]                   ; these are the blocks, like 'has-food' or go forward
breed [sensors sensor]                 ; these collect information about the ant-food-grab world
breed [agent-kinds kind]               ; process information
breed [args arg]                       ;these tell blocks what kind of things you want to do, like setting the step-size

;;NEEDED FOR MEASURE LINKING
breed [ measurepoints measurepoint ] 
measurepoints-own [
 
 tagentkind 
 tcolor 
 tcycles 
 tpopulation 
 antEnergy
 theading 
 todometer 
 tdistfromlast 
 tspeed 
 taccel 
 tpenwidth 
 tpencolor 
 measurepoint-creator 
 spiderpopulation
 frogpopulation
]
 

wabbits-own 
[
	
	pen-was-down 
                  
  
     agent-kind-string
     
     
     flag-counter        
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
     energy
     age
     of-preference
     skin-hydrocarbon
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
  
  block-name 
  arg-list
  is-observer
  is-basic
  is-set-update
  display-name
  return-type 
  label-after-arg 
  category  
]

sensors-own
[
  sensor-name
  sensor-type
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

globals
   [
    
    future-x 
    showLable?
    number-of-steps
    color-for-draw
    
    ;;NEEDED FOR MEASURE LINKING
    measure-points
    
    wabbits-list
    wabbit-kind-list
    
    
    blocks-list 
    
    
    sensors-list 
    
   
    agent-kind-list 
    predicate-list
    comp-int-left-vars
    comp-vars-left-vars
    comp-vars-right-vars
    

    chart-data-name-list 
    
    
    categories-list
        
    measure-option-string-lists
    measure-option-command-list
    
    var-name-list
    
    called-set-name
    
    can-highlight-agents
    last-cycle
    
    NaN
   ]




to setup 

  
  clear-all
  ask patches [set is-food false set is-ant-nest false set is-flying-ant-nest false]
  set-defaults
   color-background-patches
  ;make-other-stuff
  make-observer
  ;ask wabbits [ht]
  create-blocks-list  
  create-sensors-list
  create-agent-kind-list
  
  ask blocks [ht]       
  ask args [ht]
  ask agent-kinds [ht]
  
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
 set showLable? true
  reset-ticks
end

to setup-cycle ;
 ; ask patches [if ticks = 0 [set ppaint-color green] if is-nest [ set ppaint-color brown]]
 ; ask wabbits [if ticks = 0 [set paint-color red]]
 ;ask patches with [pheromone-scent <= 3] 
   
  ; [set  pcolor ppaint-color]
   ;diffuse ppaint-color .5
  
end


to takedown-cycle
  place-measure-point
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
    ;;set pcolor scale-color green pheromone-scent 0.1 5  
   ;; if  ]   ;; visual clutter.
  ;if not is-nest and not is-food and pheromone-scent <= .1
  ;[set pcolor ppaint-color]
 
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


to-report get-list-as-csv [list-name]
  let retn ""
  foreach list-name
  [
   set retn (word retn ? "," )
  ]
  report butlast retn
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
        set result result / (count wabbits with [ agent-kind-string = current-agent-kind] )
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

to create-predicate-list ;the list of reporters that the program checks against for if/otherwise block
  set predicate-list ["There-is-food-here" "I-am-holding-food" "at-ant-nest" "at-flying-ant-nest"  
    "I-have-50-energy" "I-have-0-energy" "the-chance-is-50-out-of-100" "the-chance-is-10-out-of-100" "the-chance-is-1-out-of-100" 
    "at-ant" "at-flying-ant" "at-pollinator" "at-spider" "at-frog" "at-snake" "at-bird" "at-unicorn"   ]
end

to create-comp-int-list
  set comp-int-left-vars ["heading" "step-size"]
end

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

;to-report java-patch-matches-preference [aWho]
 ; let result false
  ;ask turtle aWho[
   ; if shade-of? paint-color [ppaint-color] of patch-here = true [set result true]
  ;]
  ;report result
;end

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
    ht
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  
  create-blocks 1
  [
    set block-name "make-ants"
    set is-observer false
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 5
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
  
  create-blocks 1
  [
    set block-name "make-flying-ants"
    set is-observer false
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 5
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
  
  create-blocks 1
  [
    set block-name "make-pollinators"
    set is-observer false
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 5
      set max-value 400
      set min-value 0
    ]
    set arg-list lput max-one-of args [who] arg-list
    set category "Control"
    set is-basic false
    ; other variables not applicable
    ht
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  
  create-blocks 1
  [
    set block-name "make-other-stuff"
    set category "Control"
    set arg-list []
    set is-observer false
    set is-basic false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
     create-blocks 1
  [
    set block-name "make-queen-ant"
    set category "Control"
    set arg-list []
    set is-observer false
    set is-basic false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
        create-blocks 1
  [
    set block-name "make-flying-queen-ant"
    set category "Control"
    set arg-list []
    set is-observer false
    set is-basic false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
        create-blocks 1
  [
    set block-name "make-spider"
    set is-observer false
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 5
      set max-value 400
      set min-value 0
    ]
    set arg-list lput max-one-of args [who] arg-list
    set category "Control"
    set is-basic false
    ; other variables not applicable
    ht
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
      
            create-blocks 1
  [
    set block-name "make-frog"
    set is-observer false
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 5
      set max-value 400
      set min-value 0
    ]
    set arg-list lput max-one-of args [who] arg-list
    set category "Control"
    set is-basic false
    ; other variables not applicable
    ht
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  
             create-blocks 1
  [
    set block-name "make-snake"
    set is-observer false
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 5
      set max-value 400
      set min-value 0
    ]
    set arg-list lput max-one-of args [who] arg-list
    set category "Control"
    set is-basic false
    ; other variables not applicable
    ht
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  
        create-blocks 1
  [
    set block-name "make-bird"
    set is-observer false
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 5
      set max-value 400
      set min-value 0
    ]
    set arg-list lput max-one-of args [who] arg-list
    set category "Control"
    set is-basic false
    ; other variables not applicable
    ht
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  
    create-blocks 1
  [
    set block-name "make-unicorn"
    set is-observer false
    set arg-list []
    hatch-args 1
    [
      set arg-type "int"
      set default-value 5
      set max-value 400
      set min-value 0
    ]
    set arg-list lput max-one-of args [who] arg-list
    set category "Control"
    set is-basic False
    ; other variables not applicable
    ht
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
   
   
   
      
   
   create-blocks 1
  [
    set block-name "Omnivory"
    set category "Movement"
    set arg-list []
    set is-observer false
    set is-basic false
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
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
      
      ;create-blocks 1
  ;[
   ; set block-name "fly-forward"
   ; set category "Movement"
   ; set arg-list []
   ; set is-observer false
   ; set is-basic true
    ; other variables not applicable
  ;]
      ;set blocks-list lput max-one-of blocks [who] blocks-list
      
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
      
  
  
      
      
      
          create-blocks 1
  [
    set block-name "50-out-of-100-times"
    set category "Control"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
          create-blocks 1
  [
    set block-name "10-out-of-100-times"
    set category "Control"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
      
      
   create-blocks 1
  [
    set block-name "1-out-of-100-times"
    set category "Control"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
                  create-blocks 1
  [
    set block-name "has-50-energy"
    set category "Control"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
      
                       create-blocks 1
  [
    set block-name "has-0-energy"
    set category "Control"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
       
                       create-blocks 1
  [
    set block-name "at-friend"
    set category "Control"
    set arg-list []
    set is-observer false
    set is-basic true
    ; other variables not applicable
  ]
      set blocks-list lput max-one-of blocks [who] blocks-list
         
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
      set default-value 60
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
    set label-after-arg " % "
    set arg-list lput max-one-of args [who] arg-list
    set category "Movement"
    set is-basic true
    ; other variables not applicable
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
  
  
 
  
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
    set arg-list lput max-one-of args [who] arg-list
    set category "Movement"
    set is-basic true
    ; other variables not applicable
  ]
  set blocks-list lput max-one-of blocks [who] blocks-list
    
           
      
                 
      
   
  
                
 
 
                create-blocks 1
  [
    set block-name "eat-grass"
    set category "Movement"
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
    set methods-list lput "go" methods-list
    set methods-list lput "setup" methods-list
    
    
    set primitives-list []
    set primitives-list lput "place-measure-point" primitives-list
    set primitives-list lput "clear-measure-points" primitives-list
    set primitives-list lput "start-measuring" primitives-list

    set primitives-list lput "set-step-size" primitives-list
    set primitives-list lput "go-forward" primitives-list
    set primitives-list lput "right" primitives-list
    set primitives-list lput "left" primitives-list
    set primitives-list lput "random-turn" primitives-list
    set primitives-list lput "face-ant-nest" primitives-list
    set primitives-list lput "face-flying-ant-nest" primitives-list
    set primitives-list lput "face-pheromone" primitives-list
    set primitives-list lput "Omnivory" primitives-list
    
  
    set primitives-list lput "pickup-food-here" primitives-list
    set primitives-list lput "place-pheromone" primitives-list
    
    ;set primitives-list lput "create-food" primitives-list
    ;set primitives-list lput "eat-grass" primitives-list
    ;set primitives-list lput "patch-color" primitives-list
    ;set primitives-list lput "patch-matches-preference" primitives-list
    ;set primitives-list lput "teach" primitives-list
    ;set primitives-list lput "set-paintcolor" primitives-list
     ;set primitives-list lput "tandum-running" primitives-list
     ;set primitives-list lput "reproduce-ant" primitives-list
     set primitives-list lput "dropoff-food-here" primitives-list
     set primitives-list lput "add-energy" primitives-list
     set primitives-list lput "lose-energy" primitives-list
     set primitives-list lput "hide-energy-counter" primitives-list
     set primitives-list lput "show-energy-counter" primitives-list
     set primitives-list lput "agent-die" primitives-list
     ;set primitives-list lput "set-food-preference-to-here" primitives-list
     ;set primitives-list lput "eat-enemy" primitives-list
   
     set primitives-list lput "face-friend" primitives-list
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
  
;  create-agent-kinds 1
;  [
;    set name "observer"
;    
;    set methods-list []
;    set methods-list lput "go" methods-list
;    set methods-list lput "setup" methods-list
;    
;    
;    set primitives-list []
;    set primitives-list lput "make-other-stuff" primitives-list
;     set primitives-list lput "make-pollinators" primitives-list
;     set primitives-list lput "make-ants" primitives-list
;     set primitives-list lput "make-flying-ants" primitives-list
;     set primitives-list lput "make-queen-ant" primitives-list
;     set primitives-list lput "make-flying-queen-ant" primitives-list
;     set primitives-list lput "make-spider" primitives-list
;     set primitives-list lput "make-frog" primitives-list
;     set primitives-list lput "make-bird" primitives-list
;     set primitives-list lput "make-snake" primitives-list
;     set primitives-list lput "make-unicorn" primitives-list
;  ]
;  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
  create-agent-kinds 1
  [
    set name "flying-ant"
    
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
    set primitives-list lput "face-ant-nest" primitives-list
    set primitives-list lput "face-flying-ant-nest" primitives-list
    set primitives-list lput "face-pheromone" primitives-list
    
  
    set primitives-list lput "pickup-food-here" primitives-list
    set primitives-list lput "place-pheromone" primitives-list
    
    ;set primitives-list lput "create-food" primitives-list
    ;set primitives-list lput "eat-grass" primitives-list
    ;set primitives-list lput "patch-color" primitives-list
    ;set primitives-list lput "patch-matches-preference" primitives-list
    ;set primitives-list lput "teach" primitives-list
    ;set primitives-list lput "set-paintcolor" primitives-list
     ;set primitives-list lput "tandum-running" primitives-list
     ;set primitives-list lput "reproduce-ant" primitives-list
     set primitives-list lput "dropoff-food-here" primitives-list
     set primitives-list lput "add-energy" primitives-list
     set primitives-list lput "lose-energy" primitives-list
     set primitives-list lput "hide-energy-counter" primitives-list
     set primitives-list lput "show-energy-counter" primitives-list
     set primitives-list lput "agent-die" primitives-list
     ;set primitives-list lput "set-food-preference-to-here" primitives-list
   
     set primitives-list lput "face-friend" primitives-list
   ;  set primitives-list lput "eat-enemy" primitives-list
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
   create-agent-kinds 1
  [
    set name "pollinator"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []
    set primitives-list lput "place-measure-point" primitives-list
    set primitives-list lput "clear-measure-points" primitives-list
    set primitives-list lput "start-measuring" primitives-list
set primitives-list lput "go-forward" primitives-list
    set primitives-list lput "set-step-size" primitives-list
    
  
    set primitives-list lput "right" primitives-list
    set primitives-list lput "left" primitives-list
    set primitives-list lput "random-turn" primitives-list
    set primitives-list lput "face-ant-nest" primitives-list
      set primitives-list lput "face-flying-ant-nest" primitives-list
    set primitives-list lput "face-pheromone" primitives-list

    set primitives-list lput "pickup-food-here" primitives-list
    set primitives-list lput "place-pheromone" primitives-list
   ; set primitives-list lput "grow" primitives-list
    set primitives-list lput "create-food" primitives-list
    ;set primitives-list lput "pollinate" primitives-list
    ;set primitives-list lput "eat-grass" primitives-list
    set primitives-list lput "dropoff-food-here" primitives-list
   ; set primitives-list lput "patch-color" primitives-list
   ; set primitives-list lput "set-paintcolor" primitives-list

    set primitives-list lput "add-energy" primitives-list
     set primitives-list lput "lose-energy" primitives-list
     set primitives-list lput "hide-energy-counter" primitives-list
     set primitives-list lput "show-energy-counter" primitives-list
      set primitives-list lput "agent-die" primitives-list
    ;  set primitives-list lput "set-food-preference-to-here" primitives-list
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
  create-agent-kinds 1
  [
    set name "queen-ant"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []
    set primitives-list lput "place-measure-point" primitives-list
    set primitives-list lput "clear-measure-points" primitives-list
    set primitives-list lput "start-measuring" primitives-list

    set primitives-list lput "set-step-size" primitives-list
    
  
    set primitives-list lput "right" primitives-list
    set primitives-list lput "left" primitives-list
    set primitives-list lput "random-turn" primitives-list
    set primitives-list lput "face-ant-nest" primitives-list
      set primitives-list lput "face-flying-ant-nest" primitives-list
    ;set primitives-list lput "face-pheromone" primitives-list
   set primitives-list lput "go-forward" primitives-list 
set primitives-list lput "reproduce-ant" primitives-list
    set primitives-list lput "pickup-food-here" primitives-list
    set primitives-list lput "place-pheromone" primitives-list
    ;set primitives-list lput "grow" primitives-list
   ; set primitives-list lput "randomize-patch-color" primitives-list
    ;set primitives-list lput "eat-grass" primitives-list
    ;set primitives-list lput "patch-color" primitives-list
    ;set primitives-list lput "patch-matches-preference" primitives-list
    ;set primitives-list lput "teach" primitives-list
   ; set primitives-list lput "set-paintcolor" primitives-list
     ;set primitives-list lput "tandum-running" primitives-list
     
     set primitives-list lput "add-energy" primitives-list
     set primitives-list lput "lose-energy" primitives-list
     set primitives-list lput "hide-energy-counter" primitives-list
     set primitives-list lput "show-energy-counter" primitives-list
      set primitives-list lput "agent-die" primitives-list
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
  create-agent-kinds 1
  [
    set name "queen-flying-ant"
    
    set methods-list []
    set methods-list lput "setup" methods-list
    set methods-list lput "go" methods-list
    
    set primitives-list []
    set primitives-list lput "place-measure-point" primitives-list
    set primitives-list lput "clear-measure-points" primitives-list
    set primitives-list lput "start-measuring" primitives-list

    set primitives-list lput "set-step-size" primitives-list
    
  
    set primitives-list lput "right" primitives-list
    set primitives-list lput "left" primitives-list
    set primitives-list lput "random-turn" primitives-list
    set primitives-list lput "face-ant-nest" primitives-list
      set primitives-list lput "face-flying-ant-nest" primitives-list
    ;set primitives-list lput "face-pheromone" primitives-list
   set primitives-list lput "go-forward" primitives-list 
set primitives-list lput "reproduce-flying-ant" primitives-list
    set primitives-list lput "pickup-food-here" primitives-list
    set primitives-list lput "place-pheromone" primitives-list
    ;set primitives-list lput "grow" primitives-list
   ; set primitives-list lput "randomize-patch-color" primitives-list
    ;set primitives-list lput "eat-grass" primitives-list
    ;set primitives-list lput "patch-color" primitives-list
    ;set primitives-list lput "patch-matches-preference" primitives-list
    ;set primitives-list lput "teach" primitives-list
   ; set primitives-list lput "set-paintcolor" primitives-list
     ;set primitives-list lput "tandum-running" primitives-list
     
     set primitives-list lput "add-energy" primitives-list
     set primitives-list lput "lose-energy" primitives-list
     set primitives-list lput "hide-energy-counter" primitives-list
     set primitives-list lput "show-energy-counter" primitives-list
      set primitives-list lput "agent-die" primitives-list
    ;  set primitives-list lput "eat-enemy" primitives-list
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list

create-agent-kinds 1
  [
    set name "spider"
    
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
    set primitives-list lput "face-ant-nest" primitives-list
      set primitives-list lput "face-flying-ant-nest" primitives-list
    set primitives-list lput "face-pheromone" primitives-list
    
    
    set primitives-list lput "pickup-food-here" primitives-list
    set primitives-list lput "place-pheromone" primitives-list
   ; set primitives-list lput "grow" primitives-list
    ;set primitives-list lput "randomize-patch-color" primitives-list
    set primitives-list lput "eat-grass" primitives-list
   ; set primitives-list lput "patch-color" primitives-list
    ;set primitives-list lput "patch-matches-preference" primitives-list
   ; set primitives-list lput "teach" primitives-list
   ; set primitives-list lput "set-paintcolor" primitives-list
    ; set primitives-list lput "tandum-running" primitives-list
    
     set primitives-list lput "dropoff-food-here" primitives-list
     set primitives-list lput "add-energy" primitives-list
     set primitives-list lput "lose-energy" primitives-list
     set primitives-list lput "hide-energy-counter" primitives-list
     set primitives-list lput "show-energy-counter" primitives-list
     set primitives-list lput "agent-die" primitives-list
     ;set primitives-list lput "set-food-preference-to-here" primitives-list
     
     set primitives-list lput "face-friend" primitives-list
     set primitives-list lput "eat-ant" primitives-list
      set primitives-list lput "eat-flying-ant" primitives-list
       set primitives-list lput "reproduce-spider" primitives-list
     ;  set primitives-list lput "eat-enemy" primitives-list
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
  create-agent-kinds 1
  [
    set name "frog"
    
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
    set primitives-list lput "face-ant-nest" primitives-list
      set primitives-list lput "face-flying-ant-nest" primitives-list
    set primitives-list lput "face-pheromone" primitives-list
    
    
    set primitives-list lput "pickup-food-here" primitives-list
    set primitives-list lput "place-pheromone" primitives-list
   ; set primitives-list lput "grow" primitives-list
    ;set primitives-list lput "randomize-patch-color" primitives-list
    set primitives-list lput "eat-grass" primitives-list
   ; set primitives-list lput "patch-color" primitives-list
    ;set primitives-list lput "patch-matches-preference" primitives-list
   ; set primitives-list lput "teach" primitives-list
   ; set primitives-list lput "set-paintcolor" primitives-list
    ; set primitives-list lput "tandum-running" primitives-list
    
     set primitives-list lput "dropoff-food-here" primitives-list
     set primitives-list lput "add-energy" primitives-list
     set primitives-list lput "lose-energy" primitives-list
     set primitives-list lput "hide-energy-counter" primitives-list
     set primitives-list lput "show-energy-counter" primitives-list
     set primitives-list lput "agent-die" primitives-list
     ;set primitives-list lput "set-food-preference-to-here" primitives-list
     
     set primitives-list lput "face-friend" primitives-list
     set primitives-list lput "eat-ant" primitives-list
      set primitives-list lput "eat-flying-ant" primitives-list
       set primitives-list lput "eat-spider" primitives-list
        set primitives-list lput "reproduce-frog" primitives-list
    ;    set primitives-list lput "eat-enemy" primitives-list
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
  create-agent-kinds 1
  [
    set name "snake"
    
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
    set primitives-list lput "face-ant-nest" primitives-list
      set primitives-list lput "face-flying-ant-nest" primitives-list
    set primitives-list lput "face-pheromone" primitives-list
    
    
    set primitives-list lput "pickup-food-here" primitives-list
    set primitives-list lput "place-pheromone" primitives-list
   ; set primitives-list lput "grow" primitives-list
    ;set primitives-list lput "randomize-patch-color" primitives-list
    set primitives-list lput "eat-grass" primitives-list
   ; set primitives-list lput "patch-color" primitives-list
    ;set primitives-list lput "patch-matches-preference" primitives-list
   ; set primitives-list lput "teach" primitives-list
   ; set primitives-list lput "set-paintcolor" primitives-list
    ; set primitives-list lput "tandum-running" primitives-list
    
     set primitives-list lput "dropoff-food-here" primitives-list
     set primitives-list lput "add-energy" primitives-list
     set primitives-list lput "lose-energy" primitives-list
     set primitives-list lput "hide-energy-counter" primitives-list
     set primitives-list lput "show-energy-counter" primitives-list
     set primitives-list lput "agent-die" primitives-list
     ;set primitives-list lput "set-food-preference-to-here" primitives-list
     
     set primitives-list lput "face-friend" primitives-list
     set primitives-list lput "eat-spider" primitives-list
      set primitives-list lput "eat-frog" primitives-list
       set primitives-list lput "reproduce-snake" primitives-list
    ;   set primitives-list lput "eat-enemy" primitives-list
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
  create-agent-kinds 1
  [
    set name "bird"
    
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
    set primitives-list lput "face-ant-nest" primitives-list
      set primitives-list lput "face-flying-ant-nest" primitives-list
    set primitives-list lput "face-pheromone" primitives-list
    
    
    set primitives-list lput "pickup-food-here" primitives-list
    set primitives-list lput "place-pheromone" primitives-list
   ; set primitives-list lput "grow" primitives-list
    ;set primitives-list lput "randomize-patch-color" primitives-list
    set primitives-list lput "eat-grass" primitives-list
   ; set primitives-list lput "patch-color" primitives-list
    ;set primitives-list lput "patch-matches-preference" primitives-list
   ; set primitives-list lput "teach" primitives-list
   ; set primitives-list lput "set-paintcolor" primitives-list
    ; set primitives-list lput "tandum-running" primitives-list
    
     set primitives-list lput "dropoff-food-here" primitives-list
     set primitives-list lput "add-energy" primitives-list
     set primitives-list lput "lose-energy" primitives-list
     set primitives-list lput "hide-energy-counter" primitives-list
     set primitives-list lput "show-energy-counter" primitives-list
     set primitives-list lput "agent-die" primitives-list
     ;set primitives-list lput "set-food-preference-to-here" primitives-list
     
     set primitives-list lput "face-friend" primitives-list
     set primitives-list lput "eat-frog" primitives-list
     set primitives-list lput "eat-snake" primitives-list
     set primitives-list lput "reproduce-bird" primitives-list
        ;       set primitives-list lput "eat-enemy" primitives-list
          
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
  
  create-agent-kinds 1
  [
    set name "unicorn"
    
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
    set primitives-list lput "face-ant-nest" primitives-list
      set primitives-list lput "face-flying-ant-nest" primitives-list
    set primitives-list lput "face-pheromone" primitives-list
    
    
    set primitives-list lput "pickup-food-here" primitives-list
    set primitives-list lput "place-pheromone" primitives-list
   ; set primitives-list lput "grow" primitives-list
    set primitives-list lput "create-food" primitives-list
    set primitives-list lput "eat-grass" primitives-list
   ; set primitives-list lput "patch-color" primitives-list
    ;set primitives-list lput "patch-matches-preference" primitives-list
   ; set primitives-list lput "teach" primitives-list
   ; set primitives-list lput "set-paintcolor" primitives-list
    ; set primitives-list lput "tandum-running" primitives-list
    
     set primitives-list lput "dropoff-food-here" primitives-list
     set primitives-list lput "add-energy" primitives-list
     set primitives-list lput "lose-energy" primitives-list
     set primitives-list lput "hide-energy-counter" primitives-list
     set primitives-list lput "show-energy-counter" primitives-list
     set primitives-list lput "agent-die" primitives-list
     ;set primitives-list lput "set-food-preference-to-here" primitives-list
     
     set primitives-list lput "face-friend" primitives-list
     set primitives-list lput "eat-ant" primitives-list
     set primitives-list lput "eat-flying-ant" primitives-list
     set primitives-list lput "eat-spider" primitives-list
     set primitives-list lput "eat-frog" primitives-list
     set primitives-list lput "eat-snake" primitives-list
     set primitives-list lput "eat-bird" primitives-list
     set primitives-list lput "reproduce-unicorn" primitives-list
     ; set primitives-list lput "make-other-stuff" primitives-list
;     set primitives-list lput "make-pollinators" primitives-list
;     set primitives-list lput "make-ants" primitives-list
;     set primitives-list lput "make-flying-ants" primitives-list
;     set primitives-list lput "make-queen-ant" primitives-list
;     set primitives-list lput "make-flying-queen-ant" primitives-list
;     set primitives-list lput "make-spider" primitives-list
;     set primitives-list lput "make-frog" primitives-list
;     set primitives-list lput "make-bird" primitives-list
;     set primitives-list lput "make-snake" primitives-list
;     set primitives-list lput "make-unicorn" primitives-list
     
  ]
  set agent-kind-list lput max-one-of agent-kinds [who] agent-kind-list
end




to java-place-pheromone
  set pheromone-scent pheromone-scent + 60
end


;created by Ashlyn!!
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




;; edited by Kit and Jordan
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
  jump bonus-speed
 ; if shape = "flying-ant2" [set shape "flying-ant1"]
  ask wabbits with [agent-kind-string = "flying-ant"][
  ifelse shape = "flying-ant1" [set shape "flying-ant2"]
  [set shape "flying-ant1"]
  ]
  ;;turtle variables that will be harvested at meaure points.
 ; set odometer odometer + moved
;  if any? measurepoints
 ; [
;    if distfromlast = NaN
;    [set distfromlast 0]
;    set distfromlast distfromlast + moved
end

;to java-fly-forward
  ;jump bonus-speed
  ;set shape "flying-ant1"
  ;set shape "flying-ant2"
;end

to java-right [amount-number]
  right amount-number
end

to java-left [amount-number]
  left amount-number
end

to java-lose-energy [lostEnergy]
  set energy energy - lostEnergy
end

to java-random-turn [amount-number]
  let rand (random-float amount-number * 2)
  right rand - amount-number
end

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


to-report pheromone-scent-at-angle [angle]
  let p patch-right-and-ahead angle 1
  if p = nobody [ report 0 ]
  report [pheromone-scent] of p
end

to java-face-ant-nest
  facexy 0 0
end

to java-face-flying-ant-nest
  facexy -40 -40
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
   ht
  ]
  set odistfromlast distfromlast
  set distfromlast 0
end


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
end




;to java-grow-to [asize]
 ;  if size < 12 [set size size + asize]
;end


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
  ;ask wabbits
      ; [set size 5
     ;   set pen-size 5
        
       ; set has-food false
        ; pen is up now
       ; set pen-was-down false 
       ; set bonus-speed 2
       ; set flag-counter 0
        
       ; set secret-number random 101    ;SECRET NUMBER
       ; set repeat-num random 5 + 2     ;REPEAT NUMBER
        
       ; set initial-x xcor
       ; set initial-y ycor
       ; set previous-x ""
       ; set previous-y ""
       ;]
  
end
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

  
to java-add-energy [addedEnergy]
  set energy energy + addedEnergy
end


to java-dropoff-food-here [afoodprob]
   set has-food false
   set shape "ant"
   if random 100 < afoodprob 
   [
     set is-food true
     set is-depleted false
   ]
end


to java-agent-die
  die
end

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

to java-eat-frog
   let food-radius 2
  if any? wabbits in-radius food-radius with [agent-kind-string  = "frog"]
  [let prey one-of wabbits in-radius food-radius with [agent-kind-string = "frog"  ]
    
    if prey != nobody
    [if [energy] of prey > 0 
      [set energy energy + [energy] of prey]
      ask prey [die]    ]
  ]
    
end
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

to java-eat-grass
  if not is-depleted and not is-ant-nest and not is-flying-ant-nest
  [ 
    set is-food false
    set is-depleted true
    ask patch-here [set pcolor  (random 6) + 32]  ;; patch a random shade of brown
    set energy energy + 1
  ]
end


;to java-teach
 ; let student one-of wabbits with [shape = "ant"]
  ;if student != nobody [
 ; ask student[set paint-color [paint-color] of myself]
  ;]
  ;end
  




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

;to make-other-stuff
;  create-wabbits 10
;        [setxy random-xcor random-ycor
;        set heading random 360
;        set color red
;        set shape "ant"
;        set size 3
;        set energy 100
;        set agent-kind-string "ant"
;        set distfromlast NaN
;        set odistfromlast NaN
;       ]
;  create-wabbits 10
;        [setxy random-xcor random-ycor
;        set heading random 360
;        set color red
;        set shape "flying-ant1"
;        set size 3
;        set energy 100
;        set agent-kind-string "flying-ant"
;        set distfromlast NaN
;        set odistfromlast NaN
;       ]
;       create-wabbits 1
;        [setxy 25 25
;        set heading 90
;        set color yellow
;        set paint-color color
;        set size 4
;        set shape "bug"
;        set agent-kind-string "pollinator"
;        set distfromlast NaN
;        set odistfromlast NaN
;       ]
;        
;      create-wabbits 1
;        [setxy -25 -25
;        set heading 90
;        set color yellow
;        set paint-color color
;        set size 4
;        set shape "bug"
;        set agent-kind-string "pollinator"
;        set distfromlast NaN
;        set odistfromlast NaN
;       ]
;        
;        create-wabbits 1
;        [setxy 25 -25
;        set heading 90
;        set color yellow
;        set size 4
;        set paint-color color
;        set shape "bug"
;        set agent-kind-string "pollinator"
;        set distfromlast NaN
;        set odistfromlast NaN
;       ]
;        
;           create-wabbits 1
;        [setxy 0 0
;        set heading 90
;        set color red
;        set size 8
;        set paint-color color
;        set shape "ant"
;        set agent-kind-string "queen-ant"
;        set distfromlast NaN
;        set odistfromlast NaN
;       ]
;        
;        create-wabbits 1
;        [setxy -40 -40
;        set heading 90
;        set color red
;        set size 8
;        set paint-color color
;        set shape "flying-ant1"
;        set agent-kind-string "queen-flying-ant"
;        set distfromlast NaN
;        set odistfromlast NaN
;       ]
;        
;        create-wabbits 1
;        [setxy random-xcor random-ycor
;        set heading random 360
;        set color grey
;        set shape "spider"
;        set size 6
;        set energy 100
;        set agent-kind-string "spider"
;        set distfromlast NaN
;        set odistfromlast NaN
;       ]
;        
;        create-wabbits 1
;        [setxy random-xcor random-ycor
;        set heading random 360
;        set color grey
;        set shape "frog"
;        set size 7
;        set energy 100
;        set agent-kind-string "frog"
;        set distfromlast NaN
;        set odistfromlast NaN
;       ]
;        
;        create-wabbits 1
;        [setxy random-xcor random-ycor
;        set heading random 360
;        set color grey
;        set shape "snake"
;        set size 8
;        set energy 100
;        set agent-kind-string "snake"
;        set distfromlast NaN
;        set odistfromlast NaN
;       ]
;        
;        create-wabbits 1
;        [setxy random-xcor random-ycor
;        set heading random 360
;        set color grey
;        set shape "bird"
;        set size 9
;        set energy 100
;        set agent-kind-string "bird"
;        set distfromlast NaN
;        set odistfromlast NaN
;       ]
;        
;        create-wabbits 1
;        [setxy random-xcor random-ycor
;        set heading random 360
;        set color grey
;        set shape "unicorn"
;        set size 10
;        set energy 100
;        set agent-kind-string "unicorn"
;        set distfromlast NaN
;        set odistfromlast NaN
;       ]
;        
;        ask wabbits
;       [
;        set pen-size 5
;        
;        set has-food false
;        ; pen is up now
;        set pen-was-down false 
;        set bonus-speed 2
;        set flag-counter 0
;        
;        set secret-number random 101    ;SECRET NUMBER
;        set repeat-num random 5 + 2     ;REPEAT NUMBER
;        
;        set initial-x xcor
;        set initial-y ycor
;        set previous-x ""
;        set previous-y ""
;        ;if agent-kind-string != "queen-ant" or agent-kind-string != "pollinator" [set size 3]
;       ]
;end
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

to java-make-ants [aPopulation]
  hatch aPopulation
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
        set label energy
        show label
       ]
        
        
end

to java-make-flying-ants [aPopulation]
  hatch aPopulation
        [setxy random-xcor random-ycor
        set heading random 360
        set color red
        set shape "flying-ant1"
        set size 3
        set energy 100
        set agent-kind-string "flying-ant"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
        set label energy
        show label
       ]
        
end

to java-make-pollinators [aPopulation]
  let randnum random 3
  hatch aPopulation
        [setxy random-xcor random-ycor 
        set heading random 360
        if randnum = 1 [set color orange]
        if randnum = 2 [set color yellow]
        if randnum = 3 [set color magenta]
        set paint-color color
        set size random 5
        if randnum = 1 [set shape "bug"]
        if randnum = 2 [set shape "squirrel"]
        if randnum = 3 [set shape "plant"]
        set agent-kind-string "pollinator"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
        set label energy
        show label
       ]
        
        
end

to java-make-queen-ant
  hatch 1
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
        set label energy
        show label
       ]
  
  ask patches with [distancexy 0 0 < 4]
  [
    set is-ant-nest true
    set pcolor brown
  ]
  
  
end

to java-make-flying-queen-ant
   hatch 1
        [setxy -40 -40
        set heading 90
        set color yellow
        set size 8
        set paint-color color
        set shape "flying-ant1"
        set agent-kind-string "queen-flying-ant"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
        set label energy
        show label
       ]
        
        ask patches with [distancexy -40 -40 < 4]
  [
    set is-flying-ant-nest true
    set pcolor brown
  ]
        
        
end

to java-make-spider [aPopulation]
    hatch aPopulation
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
        set label energy
        show label
       ]
end

to java-make-frog [aPopulation]
    hatch aPopulation
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
        set label energy
        show label
       ]
end

to java-make-bird [aPopulation]
  hatch 1
        [setxy random-xcor random-ycor
        set heading random 360
        set color grey
        set shape "bird"
        set size 9
        set energy 100
        set agent-kind-string "bird"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
        set label energy
        show label
       ]
end
  

to java-make-snake [aPopulation]
    hatch aPopulation
        [setxy random-xcor random-ycor
        set heading random 360
        set color grey
        set shape "snake"
        set size 8
        set energy 100
        set agent-kind-string "snake"
        set distfromlast NaN
        set odistfromlast NaN
        set label energy
        show label
        show-turtle
       ]
end

to java-make-unicorn [aPopulation]
  hatch aPopulation
        [setxy random-xcor random-ycor
        set heading random 360
        set color grey
        set shape "unicorn"
        set size 10
        set energy 100
        set agent-kind-string "unicorn"
        set distfromlast NaN
        set odistfromlast NaN
        set label energy
        show label
        show-turtle
       ]
end
  

to java-hide-energy-counter
  set label ""
  set showLable? false
end

to java-show-energy-counter
  set label energy
  set showLable? true
end
  


to java-make-other-stuff
  hatch 10
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
  hatch 10
        [setxy random-xcor random-ycor
        set heading random 360
        set color red
        set shape "flying-ant1"
        set size 3
        set energy 100
        set agent-kind-string "flying-ant"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
       ]
       hatch 1
        [setxy 25 25
        set heading 90
        set color yellow
        set paint-color color
        set size 4
        set shape "bug"
        set agent-kind-string "pollinator"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
       ]
        
      hatch 1
        [setxy -25 -25
        set heading 90
        set color yellow
        set paint-color color
        set size 4
        set shape "bug"
        set agent-kind-string "pollinator"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
       ]
        
       hatch 1
        [setxy 25 -25
        set heading 90
        set color yellow
        set size 4
        set paint-color color
        set shape "bug"
        set agent-kind-string "pollinator"
        set distfromlast NaN
        set odistfromlast NaN
        show-turtle
       ]
        
           hatch 1
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
        
        hatch 1
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
        
        hatch 1
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
        
        hatch 1
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
        
        hatch 1
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
        
        hatch 1
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
        
        hatch 1
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

ant-has-food
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
Circle -1184463 true false 138 21 23

arrow
true
0
Polygon -7500403 true true 150 0 0 150 105 150 105 293 195 293 195 150 300 150

bird
true
0
Polygon -955883 true false 135 195 150 165
Circle -11221820 true false 86 86 127
Circle -13791810 true false 195 150 30
Polygon -13345367 true false 210 150 195 150 255 120 225 165 255 165 210 180 225 225 195 180
Circle -13791810 true false 195 150 30
Circle -11221820 true false 90 45 60
Polygon -6459832 true false 135 210 135 240 120 225 135 255 150 210 135 210
Polygon -6459832 true false 150 210 180 255 195 225 180 240 165 210 150 210
Polygon -13791810 true false 90 135 60 120 30 105 45 150 90 165 90 135
Polygon -13791810 true false 195 105 210 60 240 105 210 120 195 105
Polygon -13345367 true false 135 60 150 30 180 75 150 90
Polygon -6459832 true false 120 45 90 30 90 75
Circle -1 true false 105 45 30
Circle -16777216 true false 103 51 18

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
Circle -13345367 true false 96 182 108
Circle -13345367 true false 110 127 80
Circle -13345367 true false 110 75 80
Line -13345367 false 150 100 80 30
Line -13345367 false 150 100 220 30

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

flying-ant1
true
13
Polygon -2674135 true false 136 61 129 46 144 30 119 45 124 60 114 82 97 37 132 10 93 36 111 84 127 105 172 105 189 84 208 35 171 11 202 35 204 37 186 82 177 60 180 44 159 32 170 44 165 60
Polygon -2674135 true false 150 95 135 103 139 117 125 149 137 180 135 196 150 204 166 195 161 180 174 150 158 116 164 102
Polygon -2674135 true false 149 186 128 197 114 232 134 270 149 282 166 270 185 232 171 195 149 186
Polygon -16777216 true false 80 254 107 201 149 190 150 194 110 205
Polygon -16777216 true false 235 252 210 200 163 192 161 196 208 204
Polygon -11221820 true false 135 135 75 75 45 75 15 120 30 150 135 165
Polygon -11221820 true false 165 135 225 75 255 75 285 120 270 150 165 165

flying-ant2
true
13
Polygon -2674135 true false 136 61 129 46 144 30 119 45 124 60 114 82 97 37 132 10 93 36 111 84 127 105 172 105 189 84 208 35 171 11 202 35 204 37 186 82 177 60 180 44 159 32 170 44 165 60
Polygon -2674135 true false 150 95 135 103 139 117 125 149 137 180 135 196 150 204 166 195 161 180 174 150 158 116 164 102
Polygon -2674135 true false 149 186 128 197 114 232 134 270 149 282 166 270 185 232 171 195 149 186
Polygon -16777216 true false 80 254 107 201 149 190 150 194 110 205
Polygon -16777216 true false 235 252 210 200 163 192 161 196 208 204
Polygon -11221820 true false 135 165 75 225 45 225 15 180 30 150 135 135
Polygon -11221820 true false 165 165 225 225 255 225 285 180 270 150 165 135

frog
true
0
Circle -14835848 true false 86 116 127
Circle -14835848 true false 96 66 108
Circle -14835848 true false 108 47 41
Circle -14835848 true false 148 48 41
Circle -1 true false 116 53 24
Circle -1 true false 156 53 24
Circle -16777216 true false 124 64 10
Circle -16777216 true false 163 62 10
Polygon -2674135 true false 129 110 142 118 164 118 174 110
Polygon -1184463 true false 88 165 72 155 59 186 72 207 81 225 57 235 58 252 78 254 98 245 109 228
Polygon -1184463 true false 212 169 228 159 241 190 228 211 219 229 243 239 242 256 222 258 202 249 191 232
Polygon -1184463 true false 125 235 106 251 116 255 134 252 138 231 143 202 142 188 123 191
Polygon -1184463 true false 175 235 194 251 184 255 166 252 162 231 157 202 158 188 177 191

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

snake
true
0
Polygon -1184463 true false 66 112 43 109 29 95 27 81 34 68 48 53 69 54 83 68 88 82 90 96 104 97 119 90 145 82 175 82 202 105 214 156 211 187 213 210 226 217 244 224 273 210 273 188 282 174 289 191 288 211 274 233 256 240 237 246 225 245 207 240 193 230 183 214 182 198 185 171 184 157 182 144 179 128 166 120 135 113 120 123 82 125 73 118 66 114
Circle -14835848 true false 77 102 14
Circle -2674135 true false 97 103 14
Circle -14835848 true false 119 96 14
Circle -2674135 true false 143 90 14
Circle -14835848 true false 165 98 14
Circle -2674135 true false 179 109 18
Circle -14835848 true false 189 136 14
Circle -2674135 true false 190 159 14
Circle -14835848 true false 190 182 14
Circle -2674135 true false -61 159 14
Circle -14835848 true false 212 220 14
Circle -2674135 true false 233 225 14
Circle -14835848 true false 258 216 14
Circle -14835848 true false 258 216 14
Circle -2674135 true false 192 205 14
Line -2674135 false 40 67 19 51
Line -2674135 false 19 50 19 38
Line -2674135 false 18 52 8 53
Circle -1 true false 55 62 18
Circle -1 true false 39 79 18
Circle -16777216 true false 56 62 14
Circle -16777216 true false 39 79 14

spider
true
0
Circle -7500403 true true 105 148 92
Circle -7500403 true true 126 109 46
Rectangle -16777216 true false 75 150 90 165
Polygon -16777216 true false 130 167 108 130 108 91 91 91 92 130 119 168 123 156
Polygon -16777216 true false 110 183 75 158 72 119 56 120 59 157 110 194
Polygon -16777216 true false 124 226 85 255 91 287 106 282 104 259 132 238
Polygon -16777216 true false 116 199 68 210 59 245 72 246 77 215 119 204
Polygon -16777216 true false 176 160 188 136 188 95 204 95 202 137 181 172 172 160
Polygon -16777216 true false 192 195 240 152 240 121 219 124 219 151 186 180
Polygon -16777216 true false 186 217 237 223 236 252 215 252 216 233 180 220
Polygon -16777216 true false 175 231 208 253 208 276 183 276 183 257 164 235
Polygon -7500403 true true 133 116 123 95 146 74 133 99
Polygon -7500403 true true 164 110 172 95 154 80 164 114
Circle -16777216 true false 134 116 2
Circle -16777216 true false 132 119 4
Circle -16777216 true false 137 116 4
Circle -16777216 true false 139 123 3
Circle -16777216 true false 154 114 4
Circle -16777216 true false 159 123 6
Circle -16777216 true false 161 113 9
Polygon -2674135 true false 143 202 128 230 173 228 119 180 168 183

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

surprise-shape
true
0
Polygon -1184463 true false 136 61 129 46 144 30 119 45 124 60 114 82 97 37 132 10 93 36 111 84 127 105 172 105 189 84 208 35 171 11 202 35 204 37 186 82 177 60 180 44 159 32 170 44 165 60
Polygon -955883 true false 150 95 135 103 139 117 125 149 137 180 135 196 150 204 166 195 161 180 174 150 158 116 164 102
Polygon -14835848 true false 149 186 128 197 114 232 134 270 149 282 166 270 185 232 171 195 149 186
Polygon -16777216 true false 225 66 230 107 159 122 161 127 234 111 236 106
Polygon -16777216 true false 78 58 99 116 139 123 137 128 95 119
Polygon -16777216 true false 48 103 90 147 129 147 130 151 86 151
Polygon -16777216 true false 65 224 92 171 134 160 135 164 95 175
Polygon -16777216 true false 235 222 210 170 163 162 161 166 208 174
Polygon -16777216 true false 249 107 211 147 168 147 168 150 213 150
Circle -1 true false 114 69 42
Circle -1 true false 144 69 42
Circle -13791810 true false 128 83 14
Circle -13791810 true false 157 82 16
Polygon -2674135 true false 170 66 167 61 140 60 138 65 170 67
Line -16777216 false 125 107 120 118
Line -16777216 false 134 110 134 120
Line -16777216 false 143 109 144 123
Line -16777216 false 162 109 160 120
Line -16777216 false 168 109 168 123
Line -16777216 false 175 112 180 124
Circle -11221820 true false 132 129 16
Circle -2064490 true false 150 205 14
Circle -2064490 true false 128 220 12
Circle -8630108 true false 148 240 14
Circle -13840069 true false 151 147 16

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
Polygon -14835848 true false 119 75 179 75 209 101 224 135 220 225 175 261 128 261 81 224 74 135 88 99
Circle -1 true false 120 15 30
Circle -1 true false 150 15 30
Circle -13345367 true false 122 19 18
Circle -13345367 true false 153 18 18

unicorn
true
0
Polygon -1 true false 93 134 70 148 44 155 26 145 27 125 49 109 81 91 91 86 104 84 120 86 129 102 133 126 150 130 204 136 230 134 251 132 256 156 255 215 250 235 239 242 230 233 230 208 232 182 232 178 231 209 215 243 203 244 201 225 218 205 221 195 136 192 131 220 116 255 99 258 86 258 82 248 97 238 114 227 117 203 117 189 116 212 94 222 86 238 91 241 114 227 117 197 118 187 93 174 89 155 83 143
Polygon -8630108 true false 80 93 99 36 104 85
Circle -11221820 true false 64 92 22
Circle -11221820 true false 81 91 24
Polygon -2064490 true false 54 138 66 135 55 151 40 133
Circle -16777216 true false 66 98 12
Circle -16777216 true false 85 98 12
Polygon -5825686 true false 104 85 119 84 133 103 135 118 146 132 148 140 136 154 115 129 113 105 106 92 98 89
Polygon -5825686 true false 98 87 69 89 62 82 80 79
Polygon -5825686 true false 238 131 255 128 276 156 283 184 275 200 262 195 251 169 246 139
Line -2064490 false 100 52 90 59
Line -2064490 false 102 62 86 72
Line -2064490 false 102 70 83 81
Line -16777216 false 67 96 64 89
Line -16777216 false 72 94 72 84
Line -16777216 false 77 93 80 88
Line -16777216 false 90 94 87 85
Line -16777216 false 94 92 98 83
Line -16777216 false 99 96 104 90

unicorn1
true
15
Rectangle -11221820 true false 120 135 210 180
Circle -13840069 true false 60 75 90
Rectangle -2674135 true false 120 180 135 210
Rectangle -2674135 true false 195 180 210 210
Circle -16777216 true false 105 90 30
Circle -16777216 true false 75 105 30
Line -16777216 false 105 150 120 150
Rectangle -11221820 true false 75 105 90 120
Rectangle -11221820 true false 105 90 120 105
Line -6459832 false 210 150 225 180
Line -1184463 false 60 45 75 90

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
