# Solar-Powered-Building-App

A mobile app for a solar-powered system was my final project as a Senior at Texas A&M.
The collection of main nessecary files can be found here with a description of available feature.

The graphical interface was coded using XML.

The development was coded using Java, while communicating with a Firebase Realtime Database.

<h2>System Overview:</h2>

<img width="458" alt="Screenshot 2024-05-17 150659" src="https://github.com/edkjr10/Solar-Powered-Building-App/assets/169074953/c4714393-e890-4522-b954-389c60892292">

This project serves as a scaled down version of a solar-powered system used to implemented at someones home. Solar energy is captured by a solar panel, while a Maximum Power Point Tracker ensures that the maximal power is extracted from the energy. This power is then routed to one of two batteries by a microcontroller. The batter not being charged can be used to supply power two loads. DC power from the battery is routed through an AC Inverter in order to supply an AC load, or routed through a Buck Converter to supply a DC load. System data is recorded by the microcontroller and stored in a Firebase Realtime Database.

The mobile app serves as the user's primary method of interaction with the system. 

The app collects all necessary data from the database and maninpulates it for the user's needs, while also giving the user control over certain aspects of the system.


<h2>Main Interface:</h2>


![main1](https://github.com/edkjr10/Solar-Powered-Building-App/assets/169074953/9498408a-9ac3-4ac5-b3e3-84d298f800a9)   ![main2](https://github.com/edkjr10/Solar-Powered-Building-App/assets/169074953/413b9cd7-19a0-4df0-ab8f-7aae407450cb)   ![main3](https://github.com/edkjr10/Solar-Powered-Building-App/assets/169074953/52e40ad0-d102-430a-bb47-9f4478167554)   ![menu](https://github.com/edkjr10/Solar-Powered-Building-App/assets/169074953/0326209a-2eb0-46d0-bd96-f15dfe7f9c67)

The Main Interface offers information about the current state of the system in a digestible format:

- Current Charge Mode
- Lifetime Total Power Generated in Watts
- Lifetime Carbon Dioxide Emmision Avoided in Tons
- Total Power Consumed by Loads
- Indication of which Battery is being charged
- Indication of which Battery is supply power to the loads
- Access to further functionalities the app via a menu located at the top left of the screen

<h2>Additional Features:</h2>

