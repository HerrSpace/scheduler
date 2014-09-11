TODO Discrete

## Verifier

## Discrete verifier:
  OK input = source model
  actions with duration of 2

  * force the destination model

## Continuous verifier

* discrete + force the schedule

* Ensure the verification is working for every discrete constraints + core constraints in the following verifiers
	* impl
	* impl_repair
	* checker

* Quarantine, root should be evaluatable. We just don't care about the content of the plans. Just focus on the initial and the resulting omdel.

* Detection of the model size from the constraint signature ? -> at least 2 elements per set, 1 node per VM

# Reporting issues

* Error reduction

* Group redundant errors from the associated error message. This will make a bug reports.

## Evaluation

### Language

- conciseness
- easy to write, similar to a handwritten specification
- ability to express all the constraints
- hint: constraints of Corentin expressed after the language creation so captured effectively the concerns

### Practical benefits

- analyse last release. How much different bugs in the core ? in the pluggable ? 
- constraints of Corentin
- constraints of Tu
 -> bugs category
  cause:
    - corner case omission (offline w. sleeping)
  	- aggresive optimisation (due to repair mode ?)      
  	- incorrect model (iff but was implies)       
    - programming error (index error)
  consequence:
  	-> crashs
  	-> over-filtering
  	-> filtering

Test the reducer:
- reuse instances of hotdep.
-> add 1 fault
-> how much time to reduce to the fault constraint ?

### Performance overhead
- number of lines of unit tests for constraints, code coverage
- with verifiers, code coverage, nb of line saved
  /!\ better scalability in terms of engineering effort
- time to test a constraint with the unit tests
- time to test a constraint with the spec

- computation time checker vs. verifier