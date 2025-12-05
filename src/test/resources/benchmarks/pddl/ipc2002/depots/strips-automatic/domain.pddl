(define (domain hanoi)
  (:requirements :strips :typing)
  (:types disk peg)
  (:predicates 
    (on ?x - disk ?y - disk)
    (on-peg ?x - disk ?p - peg)
    (clear ?x - disk)
    (clear-peg ?p - peg)
    (smaller ?x - disk ?y - disk)
  )

  (:action move-disk-to-disk
    :parameters (?disk - disk ?from - disk ?to - disk)
    :precondition (and 
      (on ?disk ?from)
      (clear ?disk)
      (clear ?to)
      (smaller ?disk ?to)
    )
    :effect (and 
      (not (on ?disk ?from))
      (clear ?from)
      (on ?disk ?to)
      (not (clear ?to))
    )
  )

  (:action move-disk-to-peg
    :parameters (?disk - disk ?from - disk ?to - peg)
    :precondition (and 
      (on ?disk ?from)
      (clear ?disk)
      (clear-peg ?to)
    )
    :effect (and 
      (not (on ?disk ?from))
      (clear ?from)
      (on-peg ?disk ?to)
      (not (clear-peg ?to))
    )
  )

  (:action move-peg-to-disk
    :parameters (?disk - disk ?from - peg ?to - disk)
    :precondition (and 
      (on-peg ?disk ?from)
      (clear ?disk)
      (clear ?to)
      (smaller ?disk ?to)
    )
    :effect (and 
      (not (on-peg ?disk ?from))
      (clear-peg ?from)
      (on ?disk ?to)
      (not (clear ?to))
    )
  )

  (:action move-peg-to-peg
    :parameters (?disk - disk ?from - peg ?to - peg)
    :precondition (and 
      (on-peg ?disk ?from)
      (clear ?disk)
      (clear-peg ?to)
    )
    :effect (and 
      (not (on-peg ?disk ?from))
      (clear-peg ?from)
      (on-peg ?disk ?to)
      (not (clear-peg ?to))
    )
  )
)
