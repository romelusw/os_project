# Cotton Candy Monitor
Android app capable of monitoring system resources.

The following describes the structure of Cotton Candy Monitor Project:

-MonitorUtil (MU):
Engine that will provide data for the Application's Model. The MonitorUtil will provide components such as CPU, RAM, and Disk. MU will be a singleton that will require init to be called with the specified data components it would like to access. Once initialized and the polling has been defined for each component, MU will provide data to the Application's Model depending on the polling time.

	-IWorker:
	The components will follow this interface and override the function "getData," which will call private functions that will access the Android API to fetch the appropriate data. The "getData" function will return a "MUDataObject."
	
	-MUDataObject:
	This will provide data for a single metric, such as I/O per second. Other properties of MUDataObject will be value, attribute, and timestamp.

-Model:
The Model will receive data from the MU based on the polling durations and manage the data. The model will send the data to the View to display. The model will contain MUDataObjects inside an array. The array will be a fixed size and always remove the oldest objects before adding new ones.

-View:
The view(s) will display the information that the Model provides to the user depending on the current view. For example, a single view can provide a single a graph of a data point (such as CPU usage).

-Controller:
The Application will have a tab system, which the user can use to control the current view. When the user taps on a new tab, the model should tell the new view to display new information.

## User flow
> Whenever a user clicks on a process from the main process list pane
> 1. We check if the process exists
>   If not we alert the user with a popup indicating that the process has been since terminated
> 2. Display the specific metrics relevant to the process selected

https://play.google.com/store/apps/details?id=com.romelus_tran.cottoncandymonitor&hl=en
