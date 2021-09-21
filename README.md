# DevRandomSimulatorNew
DevRandomSimulator is the program, which simulates $cat /dev/random functionality. DevRandomSimulator program produces the [dev_random_replica] file at the location of your choice and constantly re-generating its content with new entropy data.

NOTE: Content of this file is being replaced with the entropy data coming from 3 different random algorithms: 1) Entropy data based upon System.nanoTime() seed 2) Entropy data based upon SecureRandom-related seed 3) Entropy data based upon UUID.randomUUID().getMostSignificantBits() seed

NOTE: results from all 3 entropy data sources are combined and re-shuffled.

Please use following commands to view produced results:

${FILE_DIR}/> cat dev_random_replica

or 
${FILE_DIR}/> cat dev_random_replica | base64
