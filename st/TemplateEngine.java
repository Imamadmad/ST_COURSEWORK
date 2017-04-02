package st;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

public class TemplateEngine {

    private static final Character TEMPLATE_START_PREFIX = '$';
    private static final Character TEMPLATE_START = '{';
    private static final Character TEMPLATE_END = '}';

    private static final String MM_KEEP = "keep-unmatched";
    private static final String MM_DELETE = "delete-unmatched";
    private static final String MM_OPT = "optimization";

    public TemplateEngine(){

    }

    public String evaluate(String templateString, EntryMap entryMap, String matchingMode){
        if (!isEvaluationPossible(templateString, entryMap)){
            return templateString;
        }
        if (!isMatchingModeValid(matchingMode)){
            matchingMode = MM_DELETE;
        }

        HashSet<Template> templates = identifyTemplates(templateString);

        ArrayList<Template> sortedTemplates = sortTemplates(templates);

        Result result = instantiate(templateString, sortedTemplates, entryMap.getEntries(), matchingMode);

        return result.getInstancedString();
    }

    private Boolean isEvaluationPossible(String templateString, EntryMap entryMap){
        if (templateString == null){
            return Boolean.FALSE;
        }
        if (templateString.isEmpty()) {
            return Boolean.FALSE;
        }
        if (entryMap == null){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private Boolean isMatchingModeValid(String matchingMode){
        return matchingMode != null && (
            matchingMode.equals(MM_KEEP) ||
            matchingMode.equals(MM_DELETE) ||
            matchingMode.equals(MM_OPT)
        );
    }

    private HashSet<Template> identifyTemplates(String templateString){
        HashSet<Template> templates = new HashSet<>();
        Stack<Integer> templateCandidates = new Stack<>();
        Integer charIndex = 0;
        Boolean underSequence = Boolean.FALSE;
        while (charIndex < templateString.length()){
            if (Character.compare(templateString.charAt(charIndex), TEMPLATE_START_PREFIX) == 0){
                underSequence = Boolean.TRUE;
                charIndex++;
                continue;
            }
            if (Character.compare(templateString.charAt(charIndex), TEMPLATE_START) == 0){
                if(underSequence){
                    templateCandidates.add(charIndex);
                }
                underSequence = Boolean.FALSE;
                charIndex++;
                continue;
            }
            if (Character.compare(templateString.charAt(charIndex), TEMPLATE_END) == 0){
                if (!templateCandidates.isEmpty()){
                    Template template;
                    Integer startIndex = templateCandidates.pop();
                    if ((startIndex + 1) == charIndex){
                        template = new Template(startIndex, charIndex, "");
                    }
                    else{
                        template = new Template(startIndex, charIndex, templateString.substring(startIndex+1, charIndex));
                    }
                    templates.add(template);
                }
                underSequence = Boolean.FALSE;
                charIndex++;
                continue;
            }
            underSequence = Boolean.FALSE;
            charIndex++;
        }
        return templates;
    }

    private ArrayList<Template> sortTemplates(HashSet<Template> templates){
        ArrayList<Template> sortedTemplates = new ArrayList<>();
        Template currentTemplate;
        Integer minLength;
        Integer startIndex;
        while (!templates.isEmpty()) {
            currentTemplate = null;
            minLength = Integer.MAX_VALUE;
            startIndex = Integer.MAX_VALUE;
            for (Template current : templates){
                if (current.getContent().length() < minLength){
                    currentTemplate = current;
                    minLength = current.getContent().length();
                    startIndex = current.getStartIndex();
                }
                else{
                    if (current.getContent().length() == minLength){
                        if (current.getStartIndex() < startIndex){
                            currentTemplate = current;
                            minLength = current.getContent().length();
                            startIndex = current.getStartIndex();
                        }
                    }
                }
            }
            if (currentTemplate != null) {
                templates.remove(currentTemplate);
                sortedTemplates.add(currentTemplate);
            }
            else{
                throw new RuntimeException();
            }
        }
        return sortedTemplates;
    }

    private Result instantiate(String instancedString, ArrayList<Template> sortedTemplates, ArrayList<EntryMap.Entry> sortedEntries, String matchingMode){
        int templatesReplaced = 0;
        Boolean replaceHappened;
        Template currentTemplate;
        EntryMap.Entry currentEntry;

        if (matchingMode.equals(MM_OPT)) {
            String delInstancedString = instancedString;
            int delTemplatesReplaced = 0;

            // We need deep copies of the template and entry arrays
            ArrayList<Template> delSortedTemplates = new ArrayList<Template>();
            for (Template template : sortedTemplates) {
                delSortedTemplates.add(new Template(template.getStartIndex(), template.getEndIndex(), template.getContent()));
            }

            ArrayList<EntryMap.Entry> delSortedEntries = new ArrayList<EntryMap.Entry>();
            EntryMap delMap = new EntryMap();
            for (EntryMap.Entry entry : sortedEntries) {
                delSortedEntries.add(delMap.new Entry(entry.getPattern(), entry.getValue(), entry.getCaseSensitive()));
            }


            for (int i = 0; i < sortedTemplates.size(); i++){   //keep-unmatched
                currentTemplate = sortedTemplates.get(i);

                for(EntryMap.Entry entry : sortedEntries) {

                    if (isAMatch(currentTemplate, entry)){
                        instancedString = doReplace(instancedString, currentTemplate, i, entry.getValue(), sortedTemplates);
                        templatesReplaced++;
                        break;
                    }
                }
            }

            for (int i = 0; i < delSortedTemplates.size(); i++) {   //delete-unmatched
                currentTemplate = delSortedTemplates.get(i);
                replaceHappened = false;

                for(EntryMap.Entry entry : delSortedEntries) {

                    if (isAMatch(currentTemplate, entry)) {
                        delInstancedString = doReplace(delInstancedString, currentTemplate, i, entry.getValue(), delSortedTemplates);
                        replaceHappened = true;
                        delTemplatesReplaced++;
                        break;
                    }
                }

                if (!replaceHappened) {
                    delInstancedString = doReplace(delInstancedString, currentTemplate, i, "", delSortedTemplates);
                }
            }

            return delTemplatesReplaced > templatesReplaced
                    ? new Result(delInstancedString, delTemplatesReplaced)
                    : new Result(instancedString, templatesReplaced);


        } else {
            // Original modes
            for (int i = 0; i < sortedTemplates.size(); i++) {   //keep unmatched
                currentTemplate = sortedTemplates.get(i);
                replaceHappened = false;

                for (int j = 0; j < sortedEntries.size(); j++) {
                    currentEntry = sortedEntries.get(j);
                    if (isAMatch(currentTemplate, currentEntry)){
                        instancedString = doReplace(instancedString, currentTemplate, i, currentEntry.getValue(), sortedTemplates);
                        replaceHappened = true;
                        break;
                    }
                }

                if (replaceHappened) {
                    templatesReplaced++;
                } else if (matchingMode.equals(MM_DELETE)) {
                    instancedString = doReplace(instancedString, currentTemplate, i, "", sortedTemplates);
                }
            }

            return new Result(instancedString, templatesReplaced);
        }
    }

    private Boolean isAMatch(Template template, EntryMap.Entry entry){
        String leftHandSide = template.getContent().replaceAll("\\s","");
        String rightHandSide = entry.getPattern().replaceAll("\\s","");
        if (entry.caseSensitive){
            return leftHandSide.equals(rightHandSide);
        }
        else{
            return leftHandSide.toLowerCase().equals(rightHandSide.toLowerCase());
        }
    }

    private String doReplace(String instancedString, Template currentTemplate, Integer currentTemplateIndex, String replaceValue, ArrayList<Template> sortedTemplates){
        Integer diff = 3 + currentTemplate.getContent().length() - replaceValue.length();
        String firstHalf;
        String secondHalf;
        if (currentTemplate.getStartIndex() == 1){
            firstHalf = "";
        }
        else{
            firstHalf = instancedString.substring(0, currentTemplate.getStartIndex()-1);
        }
        if (currentTemplate.getEndIndex() == instancedString.length()){
            secondHalf = "";
        }
        else{
            secondHalf = instancedString.substring(currentTemplate.getEndIndex()+1);
        }

        StringBuilder builder = new StringBuilder();
        builder.append(firstHalf);
        builder.append(replaceValue);
        builder.append(secondHalf);
        String updatedInstancedString = builder.toString();

        Template temp = null;
        for (int i=currentTemplateIndex+1; i<sortedTemplates.size(); i++){
            temp = sortedTemplates.get(i);
            if ((temp.getStartIndex() < currentTemplate.getStartIndex()) && (temp.getEndIndex() > currentTemplate.getEndIndex()))
            {
                sortedTemplates.get(i).setEndIndex(temp.getEndIndex() - diff);
                sortedTemplates.get(i).setContent(updatedInstancedString.substring(sortedTemplates.get(i).getStartIndex()+1, sortedTemplates.get(i).getEndIndex()));
            }
            else {
                if (temp.getStartIndex() > currentTemplate.getEndIndex()) {
                    sortedTemplates.get(i).setStartIndex(temp.getStartIndex() - diff);
                    sortedTemplates.get(i).setEndIndex(temp.getEndIndex() - diff);
                }
            }
        }
        return updatedInstancedString;
    }

    class Template {
        Integer startIndex;
        Integer endIndex;
        String content;

        Template(Integer startIndex, Integer endIndex, String content) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.content = content;
        }

        public Integer getStartIndex() {
            return startIndex;
        }

        public Integer getEndIndex() {
            return endIndex;
        }

        public String getContent() {
            return content;
        }

        public void setStartIndex(Integer startIndex) {
            this.startIndex = startIndex;
        }

        public void setEndIndex(Integer endIndex) {
            this.endIndex = endIndex;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Template template = (Template) o;

            if (getStartIndex() != null ? !getStartIndex().equals(template.getStartIndex()) : template.getStartIndex() != null)
                return false;
            if (getEndIndex() != null ? !getEndIndex().equals(template.getEndIndex()) : template.getEndIndex() != null)
                return false;
            return getContent() != null ? getContent().equals(template.getContent()) : template.getContent() == null;
        }

        @Override
        public int hashCode() {
            int result = getStartIndex() != null ? getStartIndex().hashCode() : 0;
            result = 31 * result + (getEndIndex() != null ? getEndIndex().hashCode() : 0);
            result = 31 * result + (getContent() != null ? getContent().hashCode() : 0);
            return result;
        }
    }

    class Result{
        String instancedString;
        Integer templatesReplaced;

        Result(String instancedString, Integer templatesReplaced) {
            this.instancedString = instancedString;
            this.templatesReplaced = templatesReplaced;
        }

        String getInstancedString() {
            return instancedString;
        }

        Integer getTemplatesReplaced() {
            return templatesReplaced;
        }
    }
}
