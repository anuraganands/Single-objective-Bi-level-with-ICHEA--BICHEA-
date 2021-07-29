
%% How to Run:
% 1 - simply choose "TestResults" folder. 
function colorMapTry()
    clear all
    clc

    d = uigetdir(pwd, 'Select a folder');
    
    analyseSR = false;
    titleTxt = 'Effect of T on ';
    
    if(analyseSR)
        files = dir(fullfile(d, '*20.csv'));%'*aggEin.csv')); %
        types = 3;
        maxRun = 15;
        titleTxt = [titleTxt 'Classification Accuracy in'];
    else
        files = dir(fullfile(d, '*aggEin.csv')); %
        types = 2;
        maxRun = 20; % total test cases
        titleTxt = [titleTxt ' ||F_b-F|| + ||f_b-f|| in'];
    end
    
    
    COL = 501;
    % 1 - T value,
    % 2-497 - error diff, 
    % odd rows are best error diff
    % even rows are average error diff
    % 1st row - generations: eg. 1, 3, 5, 7, ...
    % 2nd row - distance eg. 2, 4, 6, ...
    % 3rd row (if available) - SR
    

    total = size(files,1);
    
    
    totalRuns = (maxRun)*types;
    fprintf(['file name']);
    
    for(i = 1:total)
%         fprintf('proccessing file %s\n',files(i).name);
        [fid,msg] = fopen(files(i).name,'r');

        HL = 0;  %ignore header lines (first few lines)
        HC = 0;  %ignore columns (first few columns)
        result = textscan(fid, '', 'HeaderLines', HL, 'HeaderColumns', HC, 'Delimiter', ',');
        fclose(fid);
     
        newResult = cell(totalRuns,1);
        for r = 1:totalRuns
            for c = 1:COL
                newResult{r} = [newResult{r} result{c}(r)];
            end
        end
        
        eAvgDelta=[];
        eBestDelta=[];
        SR = [];
        totalIterationPlot = COL-1;
        av = 1;
        bs = 1;
        sr = 1;
        for r = 1: totalRuns
            if(mod(r,types) == types - 3) 
                tmp = newResult{r}(2:end);
                SR(sr,:) = tmp(1:totalIterationPlot);
%                 eAvgDelta(k,:) = norm_scale01(eAvgDelta(k,:));
                sr = sr+1;
            elseif(mod(r,types) == 1)% odd - best
                tmp = newResult{r}(2:end);
                eBestDelta(bs,:) = tmp(1:totalIterationPlot);
%                 eAvgDelta(k,:) = norm_scale01(eAvgDelta(k,:));
                bs = bs+1;
            elseif(mod(r,types) == types - 2) % even - average
                tmp = newResult{r}(2:end);
                eAvgDelta(av,:) = tmp(1:totalIterationPlot);
%                 eAvgDelta(k,:) = norm_scale01(eAvgDelta(k,:));
                av = av+1; 
            end
        end
              
        x = 1:totalIterationPlot;
        y = [1:maxRun];
        [X,Y] = meshgrid(x,y);
        Z = eAvgDelta;
        if(analyseSR)
            Z = SR;
        end
%         Z = eBestDelta;
%         surf(X,Y,Z)
        cMap=jet(256);
        figure('Name','Average Figure');
        [c,h] = contourf(X,Y,Z);
        set(h, 'edgecolor','none');  
        set(gca,'fontsize',18)
        colormap(cMap);
%         colormap(gray)
        cb = colorbar;
% %         for cl = 1:size(cb.TickLabels,1)
% %            if(str2num(cb.TickLabels{cl}) == 0)
% %                 cb.TickLabels{cl} = [cb.TickLabels{cl} '    SGD'];
% %            end
% %         end
%         colorbar('Ticks',[-0.06,0,0.03],...
%             'TickLabels',{'GSGD','<->','SGD'})
        xlabel('Iteration','fontsize',24,'color','b');
        ylabel('Threshold (T)','fontsize',24,'color','b');
        titleTxt = {titleTxt,strtok(files(i).name,'_')};
        title(titleTxt,'fontsize',24,'color','b');
%         {'Effect of {\rho} on Ein_G_S_G_D - Ein_S_G_D in', strtok(files(i).name,'_')});
% %         figure('Name','Best Figure');
% %         Z = eBestDelta;
% %         [c,h] = contourf(X,Y,Z);
% %         set(h, 'edgecolor','none');       
% %         colormap(cMap);
% %         colorbar

%<<option - 2      
% %         newpoints = 100;
% %         x = 1:totalIterationPlot;
% %         y = [2:maxRun];
% %         z = eAvgDelta;
% %         [xq,yq] = meshgrid(...
% %             linspace(min(min(x,[],2)),max(max(x,[],2)),newpoints ),...
% %             linspace(min(min(y,[],1)),max(max(y,[],1)),newpoints )...
% %           );
% %         BDmatrixq = interp2(x,y,z,xq,yq); %,'cubic');
% %         [c,h]=contourf(xq,yq,BDmatrixq,5); %,50);  
% %           colorbar;
%>>

%<<option - 3
%      f = figure;
%      ax = axes('Parent',f);
%      newpoints = 100;
%         x = 1:totalIterationPlot;
%         y = [2:maxRun];
%         z = eAvgDelta;
%         [xq,yq] = meshgrid(...
%             linspace(min(min(x,[],2)),max(max(x,[],2)),newpoints ),...
%             linspace(min(min(y,[],1)),max(max(y,[],1)),newpoints )...
%           );
%       BDmatrixq = interp2(x,y,z,xq,yq,'cubic');
%      h = surf(xq,yq,BDmatrixq,'Parent',ax);
%      set(h, 'edgecolor','none');
%      view(ax,[0,90]);
%      colormap(jet(256));
%      colorbar;
%>>
        
        
%<<other useful code        
% % %         contourf(peaks)
% % % colorbar('Ticks',[-5,-2,1,4,7],...
% % %          'TickLabels',{'Cold','Cool','Neutral','Warm','Hot'})       
        
% % % % %         for r = 1: totalRuns
% % % % %             if(mod(r,2) == 0) % even - average
% % % % %                 eAvgDelta = newResult{r};
% % % % %                 gens = 1:size(eAvgDelta,1)-1;
% % % % %                 mymap = [ro' gens' eAvgDelta']; 
% % % % %                 rgbplot(mymap)
% % % % %                 hold on
% % % % %                 colormap(mymap)
% % % % %             end
% % % % %         end
        

    end
end


