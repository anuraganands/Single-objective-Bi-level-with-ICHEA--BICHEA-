
%% How to Run:
% 1 - simply choose "TestResults" folder. 
function analyseData()
    clear all
    clc

    d = uigetdir(pwd, 'Select a folder');
    files = dir(fullfile(d, '*.csv'));
    COL = 12;
    % 1 - Problem,
    % 2 - RunNo,
    % 3 - Gen, 
    % 4 - Best Fitness UL - F, 
    % 5 - Best Fitness LL - f, 
    % 6 - Violations,	
    % 7 - DynamicTime,	
    % 8 - Processing Time,	
    % 9 - ||bestknown-F||,	
    % 10 - ||bestknown - f||,	
    % 11 - minError,	
    % 12 - NFC
   
    sortBy = [11,9,11,11,11,11,11,11,11,11];

    total = size(files,1);
    Bests = cell(1);
    probNo = 0;

    for(i = 1:total)
%         fprintf('proccessing file %s\n',files(i).name);
        [fid,msg] = fopen(files(i).name,'r');

        HL = 1;  %ignore header lines (first few lines)
        HC = 0;  %ignore columns (first few columns)
        result = textscan(fid, '', 'HeaderLines', HL, 'HeaderColumns', HC, 'Delimiter', ',');
        fclose(fid); 

        probNo = result{1}(1,1);
        [nval, nidx] = sort(result{sortBy(probNo)},'ascend');

        tmpBest = [];
        for c = 1:COL
            tmpBest = [tmpBest result{c}(nidx(1))];
        end
        fprintf('%d, %d, %d, %2f, %2f, %d, %d, %2f, %2f, %2f, %2f, %d\n', tmpBest);

        if max(size(Bests))<probNo
            Bests{tmpBest(1)} = tmpBest; 
        else
            Bests{tmpBest(1)}(end+1,:) = tmpBest; 
        end
        
    end
    
    fprintf('\n\nPrinting Best results....\n');
    total = max(size(Bests));
    tmpBest = [];
    for i = 1:total
        if isempty(Bests{i})
            continue;
        end
        probNo = Bests{i}(1,1);
        [nval, nidx] = sort(Bests{i}(:,sortBy(probNo)),'ascend');
        tmpBest = Bests{i}(nidx(1),:);
        fprintf('%d, %d, %d, %2f, %2f, %d, %d, %2f, %2f, %2f, %2f, %d\n', tmpBest);
    end

    fprintf('\n\nPrinting Average results....\n');
    total = max(size(Bests));
    tmpBest = [];
    for i = 1:total
        if isempty(Bests{i})
            continue;
        end
        probNo = Bests{i}(1,1);
        [nval, nidx] = sort(Bests{i}(:,sortBy(probNo)),'ascend');
        N = size(Bests{i},1);
        N = floor(N/2);
        tmpBest = Bests{i}(nidx(N),:);
        fprintf('%d, %d, %d, %2f, %2f, %d, %d, %2f, %2f, %2f, %2f, %d\n', tmpBest);
    end
    
end

% % There is an undocumented textscan() parameter 'headercolumns' to indicate the number of leading columns on the line to skip. Note for this purpose that "column" is determined using the same criteria used to determine "column" for the rest of textscan().
% % 
% % Possibly this HeaderColumns setting is only implemented if you use the undocumented format string '' (the empty string) which only works when all of the (unskipped) columns are numeric.
% % 
% % HL = 3;  %for example
% % HC = 10;  %in your case
% % result = textscan(fid, '', 'HeaderLines', HL, 'HeaderColumns', HC, 'Delimiter', ',');
% % If you want more control over your columns or do not like using undocumented parameters, then use an explicit format that throws away the unwanted data:
% % 
% % HL = 3;  %for example
% % HC = 10;  %in your case
% % NF = 1000; %1000 desired fields
% % lineformat = [repmat('%*s',1,HC) repmat('%f',1,NF)];
% % result = textscan(fid, lineformat, 'HeaderLines', HL, 'Delimiter', ',');

