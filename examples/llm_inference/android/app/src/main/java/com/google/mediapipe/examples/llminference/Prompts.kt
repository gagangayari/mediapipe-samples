package com.google.mediapipe.examples.llminference

object Prompts {
    fun EXTRACTWFHEntityPrompt(userPrompt : String) : String{
        return "Please extract the following field values for Work from Home Application from the given input_text and return them in a JSON format. Find the valid field value for the fields 'Asset' and 'Network' mentioned below in brackets and ensure that output-3 only has valid field value. In case Valid field value is not specified in the input_text-3 please convert it to a valid field value. \n" +
                "fromDate (dd/mmm/yyyy) \n" +
                "toDate (dd/mmm/yyyy)\n" +
                "fromTime (hh:mm)\n" +
                "toTime (hh:mm)\n" +
                "Approver \n" +
                "Asset (Infosys Laptop/Desktop or Personal Laptop/Desktop or Client Laptop/Desktop or Mobile/Other devices or No Asset)\n" +
                "Network (Infosys network or Client network or O365/calls)\n" +
                "\n" +
                "input_text-1: Apply Wfh on April 4, 2020 from 09:00 to 19:00 to Ram. I used my tablet to work and used Client's network for connectivity\n" +
                "output-1:\n" +
                "{  \n" +
                "  \"fromDate\": \"04-Apr-2024\",  \n" +
                "  \"toDate\": \"04-Apr-2024\",  \n" +
                "  \"fromTime\": \"09:00\",  \n" +
                "  \"toTime\": \"19:00\",  \n" +
                "  \"Approver\": \"ram\",  \n" +
                "  \"Asset\": \"Mobile/Other devices\",\n" +
                "  \"Network\": \"Client network\"\n" +
                "}  \n" +
                "\n" +
                "### \n" +
                "input_text-2: Submit the request with from date as 26 march 2021 and submit the same in to date also and submit the from time as 7AM and to time as 8PM and the approving manager as Seetha. I used Personal Laptop/Desktop and Infosys Network to work.\n" +
                "output-2:  \n" +
                "{\n" +
                "  \"fromDate\": \"26-Mar-2021\", \n" +
                "  \"toDate\": \"26-Mar-2021\", \n" +
                "  \"fromTime\": \"07:00\", \n" +
                "  \"toTime\": \"20:00\", \n" +
                "  \"Approver\": \"seetha\",\n" +
                "  \"Asset\": \"Personal Laptop\", \n" +
                "  \"Network\": \"Infosys Network\"\n" +
                "}\n" +
                "\n" +
                "###\n" +
                "input_text-3: $userPrompt\n" +
                "output-3:"
    }

    fun TOXICITYPROMPT(userPrompt : String) : String{
        return "Analyze if the given statement is offensive/toxic/hateful/insult/Neutral?\n" +
                "Statement :  $userPrompt.\n" +
                "Output : "
    }

    fun INJECTIONPROMPT(userPrompt : String) : String{
        return "Analyze if the given statement is Injection/Legit?\n" +
                "Statement : $userPrompt.\n" +
                "Output : "

    }
}