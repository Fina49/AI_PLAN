(define (problem disks-4-0)
(:domain hanoi)
(:objects 
  d1 d2 d3 d4 - disk
  peg1 peg2 peg3 - peg
)
(:init 
  (on d1 d2)
  (on d2 d3)
  (on d3 d4)
  (on-peg d4 peg1)
  (clear d1)
  (clear-peg peg2)
  (clear-peg peg3)
  (smaller d1 d2)
  (smaller d1 d3)
  (smaller d1 d4)
  (smaller d2 d3)
  (smaller d2 d4)
  (smaller d3 d4)
)
(:goal (and 
  (on d1 d2)
  (on d2 d3)
  (on d3 d4)
  (on-peg d4 peg3)
))
)