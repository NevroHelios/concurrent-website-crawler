# Color codes
RED=\033[0;31m
GREEN=\033[0;32m
YELLOW=\033[0;33m
NC=\033[0m

all: compile run clean

compile:
	@{ \
		echo -e "$(GREEN)Compiling with Maven...$(NC)"; \
		mvn -q compile -Dexec.jvmArgs="--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/sun.misc=ALL-UNNAMED"; \
	} 2> >(grep -v "WARNING: ")

run:
	@{ \
		echo -e "$(YELLOW)Running with Maven...$(NC)"; \
		mvn -q exec:java -Dexec.mainClass="crawler.Main" -Dexec.jvmArgs="--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/sun.misc=ALL-UNNAMED"; \
	} 2> >(grep -v "WARNING: ")

clean:
	@{ \
		echo -e "$(RED)Cleaning with Maven...$(NC)"; \
		mvn -q clean -Dexec.jvmArgs="--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/sun.misc=ALL-UNNAMED"; \
	} 2> >(grep -v "WARNING: ")