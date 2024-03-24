import java.util.*;

public class QueueSimulator {
    public static void main(String[] args) {
        double[] pseudoRandomNumbers = {0.8, 0.2, 0.1, 0.9, 0.3, 0.4, 0.7};
        int currentIndex = 0;

        PriorityQueue<Event> eventQueue = new PriorityQueue<>(4, Comparator.comparingDouble(e -> e.time));
        List<Event> allScheduledEvents = new ArrayList<>();
        eventQueue.add(new Event(Event.Type.ARRIVAL, 1.0)); // Initial arrival at time 1.0

        double serviceTime;
        double currentTime;
        double lastEventTime = 0;
        int amountOfServers = 1;
        int queueCapacity = 4;
        int currentQueueSize = 0;
        int losses = 0;
        double[] queueTimes = new double[queueCapacity];

        while (!eventQueue.isEmpty() && currentIndex < pseudoRandomNumbers.length) {

            Event currentEvent = eventQueue.poll();
            currentTime = currentEvent.time;

            accumulateTime(queueTimes, currentTime, lastEventTime, currentQueueSize);
            System.out.printf("%s/%s/%s/%s%n", currentEvent.type, currentTime, currentQueueSize,
                    Arrays.toString(queueTimes).replace("[", "").replace("]", ""));

            if (currentEvent.type == Event.Type.ARRIVAL) {
                lastEventTime = currentTime;

                if (currentQueueSize < queueCapacity) {
                    currentQueueSize++;
                    if (currentQueueSize <= amountOfServers){
                        double serviceRandom = generateUniformRandom(1.0, 4.0, pseudoRandomNumbers[currentIndex], "DEPARTURE", currentTime);
                        serviceTime = currentTime + serviceRandom;
                        eventQueue.add(new Event(Event.Type.DEPARTURE, serviceTime));
                        allScheduledEvents.add(new Event(Event.Type.DEPARTURE, serviceTime));
                    }
                }
                else {
                    losses++;
                }
                double arrivalRandom = generateUniformRandom(1.0, 3.0, pseudoRandomNumbers[currentIndex], "ARRIVAL", currentTime);
                eventQueue.add(new Event(Event.Type.ARRIVAL, currentTime + arrivalRandom));
                allScheduledEvents.add(new Event(Event.Type.ARRIVAL, currentTime + arrivalRandom));
                currentIndex++;

            } else if (currentEvent.type == Event.Type.DEPARTURE) {
                lastEventTime = currentTime;
                currentQueueSize--;

                if (currentQueueSize >= amountOfServers) {
                    double serviceRandom = generateUniformRandom(1.0, 4.0, pseudoRandomNumbers[currentIndex], "DEPARTURE", currentTime);
                    serviceTime = currentTime + serviceRandom;
                    eventQueue.add(new Event(Event.Type.DEPARTURE, serviceTime));
                    allScheduledEvents.add(new Event(Event.Type.DEPARTURE, serviceTime));
                    currentIndex++;
                }
            }
        }
    }

    static double generateUniformRandom(double a, double b, double x, String type, double currentTime) {
        double forecast = a + ((b - a) * x);
        //System.out.println(type + "," + (currentTime+forecast) + "," + x);
        return forecast;
    }

    static void accumulateTime(double[] queueTimes, double currentTime, double lastEventTime, int index) {
        double deltaTime = currentTime - lastEventTime;

        queueTimes[index] += deltaTime;
    }

    static class Event {
        enum Type {ARRIVAL, DEPARTURE}

        Type type;
        double time;

        public Event(Type type, double time) {
            this.type = type;
            this.time = time;
        }
    }
}
